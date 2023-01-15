package top.codewood.service;

import org.springframework.stereotype.Component;
import top.codewood.entity.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserService {

    private final Map<String, User> USER_MAP = new HashMap<>();

    {
        User test = new User();
        test.setId(1L);
        test.setUsername("test");
        test.setPassword("test123456");
        test.setRole("admin");
        test.setPermission("view,edit");
        USER_MAP.put("test", test);

        User demo = new User();
        demo.setId(2L);
        demo.setUsername("demo");
        demo.setPassword("demo123456");
        demo.setRole("user");
        demo.setPermission("view");
        USER_MAP.put("demo", demo);
    }

    public User get(String username) {
        return USER_MAP.get(username);
    }

}