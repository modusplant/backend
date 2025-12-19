package kr.modusplant.domains.account.social.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialIdentityErrorCode implements ResponseCode {
    EMPTY_PROVIDER(HttpStatus.BAD_REQUEST, "empty_provider", "제공자가 비어 있습니다. "),
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST, "invalid_provider", "제공자가 유효하지 않습니다. "),
    EMPTY_PROVIDER_ID(HttpStatus.BAD_REQUEST, "empty_provider_id", "제공자 id가 비어 있습니다. "),
    INVALID_PROVIDER_ID(HttpStatus.BAD_REQUEST, "invalid_provider_id", "제공자 id가 유효하지 않습니다. ");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
