package top.codewood.config.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public abstract class CustomUserDetailsService implements UserDetailsService {

    public abstract UserDetails loadUserByPhone(String phone);
}
