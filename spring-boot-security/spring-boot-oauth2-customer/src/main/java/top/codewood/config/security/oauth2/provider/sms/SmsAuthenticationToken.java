package top.codewood.config.security.oauth2.provider.sms;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

public class SmsAuthenticationToken extends AbstractAuthenticationToken {

    private String phone;
    private String code;

    public SmsAuthenticationToken(String phone, String code) {
        super(null);
        this.phone = phone;
        this.code = code;
        setAuthenticated(false);
    }

    public SmsAuthenticationToken(String phone, String code, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.phone = phone;
        this.code = code;
        super.setAuthenticated(true);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public Object getCredentials() {
        return code;
    }

    @Override
    public Object getPrincipal() {
        return phone;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }


}
