package kr.modusplant.global.middleware.security.models;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SiteMemberAuthToken extends AbstractAuthenticationToken {
    private Object principal;
    private String credential;

    protected SiteMemberAuthToken() {
        super(null);
        setAuthenticated(false);
    }

    // 인증 전
    public SiteMemberAuthToken(String principal, String credential) {
        super(null);
        this.credential = credential;
        this.principal = principal;
        setAuthenticated(false);
    }

    // 인증 후
    public SiteMemberAuthToken(UserDetails principal, Collection<? extends GrantedAuthority> authorities) {
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
}
