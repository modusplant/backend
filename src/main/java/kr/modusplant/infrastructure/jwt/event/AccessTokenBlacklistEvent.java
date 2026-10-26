package kr.modusplant.infrastructure.jwt.event;

import kr.modusplant.shared.exception.EmptyValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.EMPTY_VALUE;
import static kr.modusplant.shared.util.MaskUtils.maskAnyString;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessTokenBlacklistEvent {
    private final String accessToken;

    public static AccessTokenBlacklistEvent create(String accessToken) {
        if (StringUtils.isBlank(accessToken)) {
            throw new EmptyValueException(EMPTY_VALUE, "accessToken");
        }
        return new AccessTokenBlacklistEvent(accessToken);
    }

    @Override
    public @NonNull String toString() {
        return "AccessTokenBlacklistEvent[accessToken=" + maskAnyString(accessToken) + "]";
    }
}
