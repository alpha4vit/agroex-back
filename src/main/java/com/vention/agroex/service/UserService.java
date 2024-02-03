package com.vention.agroex.service;

import com.vention.agroex.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getById(Long id);

    User save(User user);

    void deleteById(Long id);

    User update(User user);

}
