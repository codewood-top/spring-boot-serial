package top.codewood.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.codewood.entity.User;
import top.codewood.service.UserService;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
public class MyRealm extends AuthorizingRealm {

    static final Logger LOGGER = LoggerFactory.getLogger(MyRealm.class);

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 需要检测用户权限的时候才会调用此方法， 例如checkRole, checkPermission
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        String username = JwtUtils.getUsername(principalCollection.toString());
        User user = userService.get(username);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRole(user.getRole());
        Set<String> permissions = new HashSet<>(Arrays.asList(user.getPermission().split(",")));
        simpleAuthorizationInfo.addStringPermissions(permissions);
        return simpleAuthorizationInfo;
    }

    /**
     * 默认使用此方法对用户账户校验
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        String username = JwtUtils.getUsername(token);

        if (username == null) {
            throw new AuthenticationException("无效的token");
        }

        User user = userService.get(username);

        if (user == null) {
            throw new AuthenticationException("用户不存在！");
        }

        if (!JwtUtils.verify(token, username, user.getPassword())) {
            throw new AuthenticationException("账号或密码错误！");
        }

        return new SimpleAuthenticationInfo(token, token, "my_realm");
    }
}
