package top.codewood.service;

import top.codewood.entity.User;

public interface UserService {

    User save(User user);

    User get(Long id);

}
