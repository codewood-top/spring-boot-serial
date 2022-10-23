package top.codewood.service;

import top.codewood.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User get(Long id);

}
