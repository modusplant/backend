package kr.modusplant.domains.account.social.domain.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SocialIdentityErrorCode implements ErrorCode {
    EMPTY_PROVIDER(HttpStatus.BAD_REQUEST.value(), "empty_provider", "제공자가 비어 있습니다. "),
    INVALID_PROVIDER(HttpStatus.BAD_REQUEST.value(), "invalid_provider", "제공자가 유효하지 않습니다. "),
    EMPTY_PROVIDER_ID(HttpStatus.BAD_REQUEST.value(), "empty_provider_id", "제공자 id가 비어 있습니다. "),
    INVALID_PROVIDER_ID(HttpStatus.BAD_REQUEST.value(), "invalid_provider_id", "제공자 id가 유효하지 않습니다. "),
    UNSUPPORTED_SOCIAL_PROVIDER(HttpStatus.FORBIDDEN.value(), "unsupported_social_provider", "지원되지 않는 소셜 로그인 방식입니다"),
    GOOGLE_LOGIN_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "google_login_fail", "구글 로그인 요청에 실패했습니다"),
    KAKAO_LOGIN_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "kakao_login_fail","카카오 로그인 요청에 실패했습니다");

    private final int httpStatus;
    private final String code;
    private final String message;
}
