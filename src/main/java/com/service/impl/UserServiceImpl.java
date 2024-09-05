package com.service.impl;

import com.entity.User;
import com.service.UserService;
import com.springmvc.annotation.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public boolean login(String name) {
        return "jack".equals(name);
    }

    @Override
    public List<User> getUsersList() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User(1,"jack","java工程师"));
        users.add(new User(2,"smith","前端工程师"));
        users.add(new User(3,"mary","架构师"));
        return users;
    }


}
