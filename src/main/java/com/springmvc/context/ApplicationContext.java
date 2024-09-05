package com.springmvc.context;

import com.springmvc.annotation.Autowire;
import com.springmvc.annotation.Controller;
import com.springmvc.annotation.RequestMapping;
import com.springmvc.annotation.Service;
import com.springmvc.handler.Handler;
import com.utils.StringUtils;
import com.utils.XMLUtils;
import org.dom4j.Element;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {
    private List<String> classPath = new ArrayList<>();//从component-scan获取到的类路径
    public ConcurrentHashMap<String, Object> ioc = new ConcurrentHashMap<>();//单例对象容器
    public List<Handler> handlers = new ArrayList<>();//访问路径对应
    private String contextPath;//javaweb 的运行目录

    public ApplicationContext() {
    }

    public ApplicationContext(String contextPath) {
        this.contextPath = contextPath;
        //System.out.println(contextPath);

    }

    //初始化工作
    public void init() {
        String springMvcPath = XMLUtils.getSpringMvcPath(contextPath);
        List<Element> elementsComponentScan = XMLUtils.getElementsComponentScan(springMvcPath);
        for (Element element : elementsComponentScan) {
            String basePackage = element.attributeValue("base-package");
            getPath(basePackage);
        }
        //处理单例对象
        executeInstance();
        //处理请求路径
        executeHandler();
        //处理自动注入
        executeAutowire();
    }

    private void executeAutowire() {
        for (Map.Entry<String, Object> stringObjectEntry : ioc.entrySet()) {
            Object value = stringObjectEntry.getValue();
            Field[] declaredFields = value.getClass().getDeclaredFields();//获取到所有字段
            for (Field declaredField : declaredFields) {
                //判断每一个字段都是否有Autowire注解
                if(declaredField.isAnnotationPresent(Autowire.class)){
                    //获取属性的对应类名称
                    String name = declaredField.getName();
                    Object o = ioc.get(name);
                    declaredField.setAccessible(true);//爆破
                    try {
                        //设置属性
                        declaredField.set(value,o);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }


    private void executeHandler() {
        for (Map.Entry<String, Object> stringObjectEntry : ioc.entrySet()) {
            //获取到它的类对象，以及对印的class类
            Object value = stringObjectEntry.getValue();
            Class<?> aClass = value.getClass();
            //如果被这个Controller注解
            if(aClass.isAnnotationPresent(Controller.class)){
            //    扫描它的方法
                Method[] methods = aClass.getMethods();
                for (Method method : methods) {
                    //判断它的方法是否被RequestMapping修饰
                    if(method.isAnnotationPresent(RequestMapping.class)){
                        //获取到url路径
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        String url = requestMapping.value();
                        handlers.add(new Handler(url,value,method));
                    }
                }
            }
        }
        for (Handler handler : handlers) {
            System.out.println("handler="+handler);
        }
    }

    private void executeInstance() {
        for (String path : classPath) {
            //反射对象
            try {
                Class<?> aClass = Class.forName(path);

                //判断是否有注解
                if (aClass.isAnnotationPresent(Controller.class)) {
                    Object o = aClass.newInstance();
                    String simpleName = aClass.getSimpleName();//类名称
                    simpleName = StringUtils.InitialLowercase(simpleName);//大小写转化
                    ioc.put(simpleName, o);
                } else if (aClass.isAnnotationPresent(Service.class)) {
                    //看一下是否是接口实现
                    Class<?>[] interfaces = aClass.getInterfaces();
                    Object o = aClass.newInstance();
                    if (interfaces.length >= 1) {
                        String simpleName=null;
                        //如果有实现接口
                        for (Class<?> anInterface : interfaces) {
                            //获取接口的名字
                            simpleName = anInterface.getSimpleName();
                            simpleName = StringUtils.InitialLowercase(simpleName);

                            //装入ioc
                            ioc.put(simpleName, o);
                        }
                    } else {
                        //如果没有实现接口
                        //按照本身类名，首字母小写作为key
                        String simpleName = aClass.getSimpleName();
                        simpleName = StringUtils.InitialLowercase(simpleName);
                        ioc.put(simpleName, o);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    //获取backPackage指定包下面的全部类的全类名
    public void getPath(String packName) {
        String replacePacName = packName.replace(".", "/");
        //System.out.println("replacePacName="+replacePacName);
        URL resource = this.getClass().getClassLoader().getResource(replacePacName);
        String path = resource.getPath();//文件的绝对路路径
        //System.out.println("path="+path);
        File fileParent = new File(path);
        //拿到子文件
        File[] files = fileParent.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    //System.out.println(packName+"."+file.getName());
                    getPath(packName + "." + file.getName());
                } else {
                    //如果是类文件
                    classPath.add(packName + "." + file.getName().replace(".class", ""));
                }
            }
        }
    }
}
