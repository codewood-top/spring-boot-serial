package top.codewood.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.controller.bean.ResultEntity;
import top.codewood.entity.User;
import top.codewood.security.JwtUtils;
import top.codewood.service.UserService;

import java.util.Optional;

@RestController
public class WebController {

    static final Logger LOGGER = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public ResultEntity login(@RequestParam("username") String username,
                              @RequestParam("password") String password) {
        User user = Optional.ofNullable(userService.get(username)).orElseThrow(() -> new RuntimeException("用户不存在！"));
        if (user.getPassword().equals(password)) {
            String token = JwtUtils.sign(username, password);
            return ResultEntity.success(token);
        } else {
            return ResultEntity.failure(401, "账号密码错误！");
        }
    }

    @RequestMapping("/info")
    public ResultEntity info() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return ResultEntity.success(String.format("username: %s", subject.getPrincipal()));
        } else {
            return ResultEntity.success("匿名用户");
        }
    }

    @RequestMapping("/auth")
    @RequiresAuthentication
    public ResultEntity auth() {
        return ResultEntity.success("已登录，可以查阅绝密资料。");
    }

    @RequestMapping("/admin")
    @RequiresRoles("admin")
    public ResultEntity admin() {
        return ResultEntity.success("具有admin");
    }

    @RequestMapping("/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResultEntity unAuthorized() {
        return ResultEntity.failure(401, "未登录");
    }

}
