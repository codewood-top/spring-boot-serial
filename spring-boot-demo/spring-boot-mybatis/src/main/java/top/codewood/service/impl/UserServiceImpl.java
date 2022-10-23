package top.codewood.service.impl;

import org.springframework.stereotype.Service;
import top.codewood.entity.User;
import top.codewood.service.UserService;
import top.codewood.service.repository.UserRepository;

import java.util.List;

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User get(Long id) {
        return userRepository.get(id);
    }
}
