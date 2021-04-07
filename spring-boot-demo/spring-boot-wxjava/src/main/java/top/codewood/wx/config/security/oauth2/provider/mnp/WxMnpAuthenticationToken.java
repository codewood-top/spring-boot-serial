package top.codewood.wx.config.security.oauth2.provider.mnp;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

public class WxMnpAuthenticationToken extends AbstractAuthenticationToken {


    private String pricipal;
    private String credentials;

    public WxMnpAuthenticationToken(String appid, String code) {
        super(null);
        this.pricipal = appid;
        this.credentials = code;
        setAuthenticated(false);
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public WxMnpAuthenticationToken(String pricipal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.pricipal = pricipal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return pricipal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

}
