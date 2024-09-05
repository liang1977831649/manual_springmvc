package com.utils;

public class StringUtils {
    //首字母小写
    public static String InitialLowercase(String name){
        return name.toLowerCase().charAt(0)+name.substring(1);
    }
}
