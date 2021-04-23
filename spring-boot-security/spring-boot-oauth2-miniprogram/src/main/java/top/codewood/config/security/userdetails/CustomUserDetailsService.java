package top.codewood.config.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import top.codewood.entity.vo.user.WxUserInfo;

public interface CustomUserDetailsService extends UserDetailsService {

    UserDetails loadUserByWxInfo(WxUserInfo wxUserInfo);
}
