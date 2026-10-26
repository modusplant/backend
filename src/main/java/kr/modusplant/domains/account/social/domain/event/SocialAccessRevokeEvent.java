package kr.modusplant.domains.account.social.domain.event;

import kr.modusplant.shared.exception.EmptyValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.EMPTY_VALUE;
import static kr.modusplant.shared.util.MaskUtils.maskAnyString;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialAccessRevokeEvent {
    private final UUID memberId;
    private final String authProvider;
    private final String socialAccessToken;

    public static SocialAccessRevokeEvent create(UUID memberId, String authProvider, String socialAccessToken) {
        if (memberId == null) {
            throw new EmptyValueException(EMPTY_VALUE, "memberId");
        }
        if (StringUtils.isBlank(authProvider)) {
            throw new EmptyValueException(EMPTY_VALUE, "authProvider");
        }
        if (StringUtils.isBlank(socialAccessToken)) {
            throw new EmptyValueException(EMPTY_VALUE, "socialAccessToken");
        }
        return new SocialAccessRevokeEvent(memberId, authProvider, socialAccessToken);
    }
    @Override
    public @NonNull String toString() {
        return "SocialAccessRevokeEvent[memberId=" + memberId +
                ", authProvider=" + authProvider +
                ", socialAccessToken=" + maskAnyString(socialAccessToken) +
                "]";
    }
}
