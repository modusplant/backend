package kr.modusplant.global.middleware.security.models;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SiteMemberAuthToken extends AbstractAuthenticationToken {
    private String email;
    private String password;

    protected SiteMemberAuthToken() {
        super(null);
        setAuthenticated(false);
    }

    // 인증 전
    public SiteMemberAuthToken(String email, String password) {
        super(null);
        this.email = email;
        this.password = password;
        setAuthenticated(false);
    }

    // 인증 후
    public SiteMemberAuthToken(UserDetails principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.email = principal.getUsername();
        this.password = principal.getPassword();
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return email;
    }

    @Override
    public Object getPrincipal() {
        return password;
    }
}
