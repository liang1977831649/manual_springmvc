package com.service.impl;

import com.entity.Monster;
import com.service.MonsterService;
import com.springmvc.annotation.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonsterServiceImpl implements MonsterService {


    @Override
    public List<Monster> getList() {
        List<Monster> monsters=new ArrayList<>();
        monsters.add(new Monster(1,"牛魔王","吹牛"));
        monsters.add(new Monster(2,"红孩儿","三味真火"));

        return monsters;
    }

    @Override
    public Monster findMonster() {
        return new Monster(3,"红孩儿","吐火");
    }
}
