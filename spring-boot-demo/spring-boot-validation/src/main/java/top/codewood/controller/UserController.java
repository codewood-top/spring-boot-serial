package top.codewood.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.entity.User;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    {
        LOGGER.info("UserController instanctiated");
    }

    @RequestMapping("create")
    public String create(@Valid User user) {
        LOGGER.info(user.toString());
        return "ok";
    }

    @RequestMapping("get")
    public String get(@NotEmpty(message = "username不能为空！") String username) {
        LOGGER.info("username: {}", username);
        return "ok";
    }

    @RequestMapping("list")
    public String list(@NotNull @Min(value = 1, message = "年龄须大于0") @Max(value = 120, message = "年龄不能超过120") Integer age) {
        LOGGER.info("age: {}", age);
        return "ok";
    }

}
