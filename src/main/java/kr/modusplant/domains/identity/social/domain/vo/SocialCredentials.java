package kr.modusplant.domains.identity.social.domain.vo;

import kr.modusplant.domains.identity.social.domain.exception.EmptyProviderException;
import kr.modusplant.domains.identity.social.domain.exception.EmptyProviderIdException;
import kr.modusplant.domains.identity.social.domain.exception.InvalidProviderException;
import kr.modusplant.domains.identity.social.domain.exception.InvalidProviderIdException;
import kr.modusplant.shared.enums.AuthProvider;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialCredentials {
    private final AuthProvider provider;
    private final String providerId;

    private static final int KAKAO_PROVIDER_ID_LENGTH = 10;
    private static final int GOOGLE_PROVIDER_ID_LENGTH = 21;

    public static SocialCredentials create(AuthProvider provider, String providerId) {
        validateSocialProvider(provider,providerId);
        return new SocialCredentials(provider, providerId);
    }

    public static SocialCredentials createKakao(String providerId) {
        validateSocialProvider(AuthProvider.KAKAO,providerId);
        return new SocialCredentials(AuthProvider.KAKAO, providerId);
    }

    public static SocialCredentials createGoogle(String providerId) {
        validateSocialProvider(AuthProvider.GOOGLE,providerId);
        return new SocialCredentials(AuthProvider.GOOGLE, providerId);
    }

    private static void validateSocialProvider(AuthProvider provider, String providerId) {
        if (provider == null) {
            throw new EmptyProviderException();
        }
        if (providerId == null || providerId.isBlank()) {
            throw new EmptyProviderIdException();
        }
        if (provider == AuthProvider.BASIC) {
            throw new InvalidProviderException();
        }
        if (provider == AuthProvider.KAKAO && providerId.length() != KAKAO_PROVIDER_ID_LENGTH) {
            throw new InvalidProviderIdException();
        }
        if (provider == AuthProvider.GOOGLE && providerId.length() != GOOGLE_PROVIDER_ID_LENGTH) {
            throw new InvalidProviderIdException();
        }
    }

    public boolean isSameProvider(AuthProvider provider) {
        return this.provider == provider;
    }

    public boolean isGoogle() {
        return this.provider == AuthProvider.GOOGLE;
    }

    public boolean isKakao() {
        return this.provider == AuthProvider.KAKAO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SocialCredentials socialCredentials)) return false;

        return new EqualsBuilder()
                .append(getProvider(), socialCredentials.getProvider())
                .append(getProviderId(), socialCredentials.getProviderId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getProvider())
                .append(getProviderId()).toHashCode();
    }
}
