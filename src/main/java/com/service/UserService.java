package com.service;

import com.entity.User;

import java.util.List;

public interface UserService {
    public boolean login(String name);
    public List<User> getUsersList();
}
