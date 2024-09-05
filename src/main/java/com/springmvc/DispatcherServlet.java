package com.springmvc;

import com.alibaba.fastjson.JSON;
import com.springmvc.annotation.RequestParam;
import com.springmvc.annotation.ResponseBody;
import com.springmvc.handler.Handler;
import com.springmvc.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DispatcherServlet extends HttpServlet {
    ApplicationContext applicationContext;
    ViewResolveDispatcher viewResolveDispatcher;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        System.out.println("已被访问");
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        System.out.println("已被访问");

        executeRequest(req, resp);
    }

    @Override
    public void init() throws ServletException {
        //SAXReader saxReader = new SAXReader();
        applicationContext = new ApplicationContext(getServletContext().getRealPath("/"));
        applicationContext.init();
        viewResolveDispatcher = new ViewResolveDispatcher();
    }

    public void executeRequest(HttpServletRequest req, HttpServletResponse resp) {
        //处理中文乱码
        try {
            req.setCharacterEncoding("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String url = req.getServletContext().getContextPath();//工程路径
        String urlPath = req.getRequestURI();//当前访问路径,/工程路径/*
        Handler handler = null;
        try {
            if (!Pattern.matches(url + "(\\/\\w+)*(.*)", urlPath)) {
                //如果不匹配，返回404
                resp.getWriter().write("<h1>404 Not Found</h1>");
                return;
            }
            //获取对应的handler
            handler = getHandler(urlPath);
            if (handler == null) {
                resp.getWriter().write("<h1>404 Not Found</h1>");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //先将req 和 resp放入到param里面
        //获取参数类型
        Class<?>[] typeParameters = handler.getMethod().getParameterTypes();
        Object[] param = new Object[typeParameters.length];
        for (int i = 0; i < typeParameters.length; i++) {
            Class<?> typeParameter = typeParameters[i];
            if ("HttpServletRequest".equals(typeParameter.getSimpleName())) {
                param[i] = req;
            } else if ("HttpServletResponse".equals(typeParameter.getSimpleName())) {
                param[i] = resp;
            }
        }
        // 赋值其他参数
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (Map.Entry<String, String[]> stringEntry : parameterMap.entrySet()) {
            String paramName = stringEntry.getKey();//遍历名
            String paramValue = stringEntry.getValue()[0];//变量值
            //找到对应的位置
            int index = getIndexParamOf(paramName, handler.getMethod().getParameters());
            if (index != -1) {
                param[index] = paramValue;
            }
        }
        Object invoke = null;
        try {
            //调用函数
            invoke = handler.getMethod().invoke(handler.getController(), param);
            if (invoke != null) {
                //如果返回的是字符串，那就就是转发或重定向到某个页面
                if (invoke instanceof String) {
                    if (((String) invoke).contains(":")) {
                        viewResolveDispatcher.redirectTo(req, resp, (String) invoke);
                    }
                    else {
                        viewResolveDispatcher.forwardTo(req, resp, (String) invoke);
                    }
                }
                //如果返回的是整个对象,并且被ResponseBody修饰
                else if(handler.getMethod().isAnnotationPresent(ResponseBody.class)){
                    resp.getWriter().write(JSON.toJSONString(invoke));
                }else{
                    resp.getWriter().write("<h1>500</h1>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Handler getHandler(String urlPath) {
        List<Handler> handlers = applicationContext.handlers;
        for (Handler handler : handlers) {
            if (urlPath.contains(handler.getUrl())) {
                return handler;
            }
        }
        return null;
    }

    public int getIndexParamOf(String paramName, Parameter[] parameters) {
        String requestParamValue = null;
        ArrayList<String> parametersList = getMethodParamNameList(parameters);

        for (int i = 0; i <= parameters.length; i++) {
            Parameter param = parameters[i];
            requestParamValue = parametersList.get(i);
            ;//获取参数名称

            if (param.isAnnotationPresent(RequestParam.class)) {
                requestParamValue = param.getAnnotation(RequestParam.class).value();
            }
            //判断是否相等
            if (requestParamValue.equals(paramName)) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<String> getMethodParamNameList(Parameter[] parameters) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Parameter parameter : parameters) {
            arrayList.add(parameter.getName());
        }
        return arrayList;
    }
}
