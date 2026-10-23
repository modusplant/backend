package kr.modusplant.infrastructure.jwt.event;

import kr.modusplant.shared.exception.EmptyValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.EMPTY_VALUE;

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
}
