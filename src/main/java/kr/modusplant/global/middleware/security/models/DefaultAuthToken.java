package kr.modusplant.global.middleware.security.models;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class DefaultAuthToken extends AbstractAuthenticationToken {
    private Object principal;
    private String credential;

    protected DefaultAuthToken() {
        super(null);
        setAuthenticated(false);
    }

    // 인증 전
    public DefaultAuthToken(String principal, String credential) {
        super(null);
        this.credential = credential;
        this.principal = principal;
        setAuthenticated(false);
    }

    // 인증 후
    public DefaultAuthToken(UserDetails principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.credential = null;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credential;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() { return super.getAuthorities(); }
}
