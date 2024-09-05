package com.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XMLUtils {
    //private static SAXReader saxReader=new SAXReader();

    //获取web.xml的<param-value>classpath:springmvc.xml</param-value>里面的springmvc.xml
    public static String getSpringMvcPath(String contextPath) {
        SAXReader saxReader = new SAXReader();
        Document read;
        String substring=null;
        try {
            read = saxReader.read(contextPath + "WEB-INF\\web.xml");
            Element rootElement = read.getRootElement();
            Element servlet = rootElement.element("servlet");
            Element initParam = servlet.element("init-param");
            Element paramValue = initParam.element("param-value");
            substring = paramValue.getText().substring(paramValue.getText().indexOf(":") + 1);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        return substring;
    }

    //    获取springmvc指定的全包名称
    public static List<Element> getElementsComponentScan(String beanPath) {
        List<Element> elements = null;
        InputStream resourceAsStream = XMLUtils.class.getClassLoader().getResourceAsStream(beanPath);
        SAXReader saxReader = new SAXReader();
        try {
            Document read = saxReader.read(resourceAsStream);
            Element rootElement = read.getRootElement();
            elements = rootElement.elements("component-scan");
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        //System.out.printl("elements="+elements);
        return elements;
    }
}
