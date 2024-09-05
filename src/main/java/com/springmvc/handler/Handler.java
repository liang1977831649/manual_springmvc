package com.springmvc.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Handler {
    private String url;
    private Object controller;
    private Method method;
}
