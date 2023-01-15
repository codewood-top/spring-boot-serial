package top.codewood.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.codewood.entity.vo.user.UserVO;
import top.codewood.http.vo.ResultEntity;
import top.codewood.service.UserService;

@RestController
@RequestMapping("/user/rest")
public class UserRestController {

    static final Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/auth_info")
    public UserVO authInfo() {
        return new UserVO(userService.get((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
    }

    @RequestMapping("/update")
    public ResultEntity update(
            @RequestParam("appid") String appid,
            @RequestParam("encryptedData") String encryptedData, @RequestParam("iv") String iv) {

        LOGGER.info("authorization: {}", SecurityContextHolder.getContext().getAuthentication());
        userService.update((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal(), appid, encryptedData, iv);
        return ResultEntity.success();
    }


}
