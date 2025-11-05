package kr.modusplant.infrastructure.security.models;

import kr.modusplant.shared.enums.AuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
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

    public static DefaultUserDetailsBuilder builder() { return new DefaultUserDetailsBuilder(); }

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

        public DefaultUserDetailsBuilder email(String email) {
            this.email = email;
            return this;
        }

        public DefaultUserDetailsBuilder password(String password) {
            this.password = password;
            return this;
        }

        public DefaultUserDetailsBuilder activeUuid(UUID activeUuid) {
            this.activeUuid = activeUuid;
            return this;
        }

        public DefaultUserDetailsBuilder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public DefaultUserDetailsBuilder provider(AuthProvider provider) {
            this.provider = provider;
            return this;
        }

        public DefaultUserDetailsBuilder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public DefaultUserDetailsBuilder isDisabledByLinking(boolean isDisabledByLinking) {
            this.isDisabledByLinking = isDisabledByLinking;
            return this;
        }

        public DefaultUserDetailsBuilder isBanned(boolean isBanned) {
            this.isBanned = isBanned;
            return this;
        }

        public DefaultUserDetailsBuilder email(boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public DefaultUserDetailsBuilder email(List<GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public DefaultUserDetails build() {
            return new DefaultUserDetails(this.email, this.password, this.activeUuid, this.nickname, this.provider, this.isActive, this.isDisabledByLinking, this.isBanned, this.isDeleted, this.authorities);
        }
    }
}
