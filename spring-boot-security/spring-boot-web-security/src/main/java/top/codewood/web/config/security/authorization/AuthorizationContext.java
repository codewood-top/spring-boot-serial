package top.codewood.web.config.security.authorization;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import top.codewood.web.entity.User;

public class AuthorizationContext {

    static final String SESSION_LOGIN_INFO = "loginInfo";

    public static LoginInfo setCurrentAuthenticationUser(User user) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUserId(user.getId());
        loginInfo.setUsername(user.getUsername());
        loginInfo.setNickname(user.getNickname());
        loginInfo.setAvatar(user.getAvatar());
        RequestContextHolder.getRequestAttributes().setAttribute(SESSION_LOGIN_INFO, loginInfo, RequestAttributes.SCOPE_SESSION);
        return loginInfo;
    }

    public static LoginInfo loginInfo() {
        Object loginInfoObj = RequestContextHolder.getRequestAttributes().getAttribute(SESSION_LOGIN_INFO, RequestAttributes.SCOPE_SESSION);
        if (loginInfoObj != null) return (LoginInfo) loginInfoObj;
        if (!isAuthorized()) throw new AuthenticationCredentialsNotFoundException("未登录或者登录信息已失效!");
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return setCurrentAuthenticationUser(User.getUser(userDetails.getUsername()));
    }

    public static boolean isAuthorized() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated() && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser");
    }

}
