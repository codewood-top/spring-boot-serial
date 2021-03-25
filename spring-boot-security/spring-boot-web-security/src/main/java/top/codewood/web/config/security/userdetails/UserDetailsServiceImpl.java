package top.codewood.web.config.security.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.codewood.web.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    private PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = User.getUser(username);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(User user) {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorityList.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorityList);
    }

}
