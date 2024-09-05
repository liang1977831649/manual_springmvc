package com.controller;

import com.entity.User;
import com.service.MonsterService;
import com.service.UserService;
import com.springmvc.annotation.Autowire;
import com.springmvc.annotation.Controller;
import com.springmvc.annotation.RequestMapping;
import com.springmvc.annotation.Service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class UserController {
    @Autowire
    private UserService userService;

    @RequestMapping(value = "/user/listTest")
    public void getList(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.write("<h1>this is user method that getList</h1>");
            System.out.println("返回用户列表");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/user/login")
    public String login(HttpServletRequest request, HttpServletResponse response, String name) {
        System.out.println("name=" + name);

        //先判空
        if (null==name) {
            return null;
        }
        request.setAttribute("name", name);
        if (userService.login(name)) {
            return "/login_success.jsp";
        }
        return "redirect:/login_fail.jsp";
    }
}
