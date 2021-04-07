package top.codewood.wx.config.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import top.codewood.wx.mp.bean.oauth2.WxOAuth2UserInfo;

public abstract class CustomUserDetailsService implements UserDetailsService {

    public abstract UserDetails loadUserByWxOpenid(String openid, Object data);

}
