package kr.modusplant.global.middleware.security.models;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.domains.member.enums.AuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Builder
public class DefaultUserDetails implements UserDetails {
    private final String email;
    private final String password;
    private final UUID activeUuid;
    private final String nickname;
    private final AuthProvider provider;
    private final boolean isActive;
    private final boolean isDisabledByLinking;
    private final boolean isBanned;
    private final boolean isDeleted;
    private final List<GrantedAuthority> authorities;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public List<GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isActive() { return isActive; }

    public boolean isDisabledByLinking() { return isDisabledByLinking; }

    public boolean isBanned() { return isBanned; }

    public boolean isDeleted() { return isDeleted; }

    public static class DefaultUserDetailsBuilder {
        private String email;
        private String password;
        private UUID activeUuid;
        private String nickname;
        private AuthProvider provider;
        private boolean isActive;
        private boolean isDisabledByLinking;
        private boolean isBanned;
        private boolean isDeleted;
        private List<GrantedAuthority> authorities;

        public DefaultUserDetailsBuilder member(
                SiteMember member, SiteMemberAuth memberAuth, SiteMemberRole memberRole) {
            this.email = memberAuth.getEmail();
            this.password = memberAuth.getPw();
            this.activeUuid = memberAuth.getActiveMemberUuid();
            this.nickname = member.getNickname();
            this.provider = memberAuth.getProvider();
            this.isActive = member.getIsActive();
            this.isDisabledByLinking = member.getIsDisabledByLinking();
            this.isBanned = member.getIsBanned();
            this.isDeleted = member.getIsDeleted();
            this.authorities = List.of(new SimpleGrantedAuthority(memberRole.getRole().getValue()));
            return this;
        }

        public DefaultUserDetails build() {
            return new DefaultUserDetails(this.email, this.password, this.activeUuid, this.nickname, this.provider, this.isActive, this.isDisabledByLinking, this.isBanned, this.isDeleted, this.authorities);
        }
    }
}
