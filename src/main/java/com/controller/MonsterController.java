package com.controller;
import com.alibaba.fastjson.JSON;
import com.entity.Monster;
import com.service.MonsterService;
import com.springmvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
@Controller
public class MonsterController {
    @Autowire
    private MonsterService monsterService;

    @RequestMapping(value = "/monster/list")
    public void getList(HttpServletRequest request, HttpServletResponse response){
        System.out.println("返回妖怪列表");
        response.setContentType("text/html;charset=utf-8");
        List<Monster> list = monsterService.getList();
        String  json = JSON.toJSONString(list);

        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            printWriter.write("<h1>返回妖怪列表</h1>");
            printWriter.write("<h2>"+json+"</h2>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/monster/find")
    public void getMonster(HttpServletRequest request,HttpServletResponse response,@RequestParam(value = "name") String userName){
        System.out.println("userName="+userName);
        Monster monster = monsterService.findMonster();
        String jsonString = JSON.toJSONString(monster);
        System.out.println(jsonString);
        response.setContentType("text/html;charset=utf-8");
        try {
            response.getWriter().write("<h1>返回一个单个对象</br>"+jsonString+"</h1>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseBody
    @RequestMapping("/monster/list")
    public List<Monster> getMonsterList(HttpServletRequest request,HttpServletResponse response){
        return monsterService.getList();
    }

    @RequestMapping("/monster/list2")
    public List<Monster> getMonsterList2(HttpServletRequest request,HttpServletResponse response){
        return monsterService.getList();
    }
}
