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
    KAKAO_LOGIN_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "kakao_login_fail","카카오 로그인 요청에 실패했습니다"),
    ALREADY_REGISTERED_WITH_KAKAO(HttpStatus.CONFLICT.value(), "already_registered_with_kakao", "이미 카카오 로그인으로 가입된 계정입니다. "),
    ALREADY_REGISTERED_WITH_GOOGLE(HttpStatus.CONFLICT.value(), "already_registered_with_google", "이미 구글 로그인으로 가입된 계정입니다. "),
    INVALID_AGREED_TERMS_OF_VERSION(HttpStatus.BAD_REQUEST.value(), "invalid_agreed_terms_of_version", "동의한 약관의 버전 값이 올바른 형식이 아닙니다"),
    EMPTY_AGREED_TERMS_OF_VERSION(HttpStatus.BAD_REQUEST.value(), "empty_agreed_terms_of_version", "동의한 약관의 버전 값이 비어 있습니다"),
    GOOGLE_REVOKE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "google_revoke_fail", "구글 연결 해제 요청이 실패했습니다. "),
    KAKAO_REVOKE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "kakao_revoke_fail","카카오 연결 해제 요청이 실패했습니다. "),
    ALREADY_SIGNED_UP(HttpStatus.CONFLICT.value(), "already_signed_up", "이미 소셜 회원가입한 계정입니다. "),
    ALREADY_LINKED(HttpStatus.CONFLICT.value(), "already_linked","이미 소셜 연동된 계정입니다. "),
    NOT_LINKED(HttpStatus.CONFLICT.value(), "not_linked", "소셜 계정이 연동되어 있지 않습니다. "),
    SOCIAL_WITHDRAWAL_REQUIRED(HttpStatus.BAD_REQUEST.value(), "social_withdrawal_required", "소셜 계정 탈퇴가 필요합니다. "),
    SOCIAL_LINKAGE_REQUIRED(HttpStatus.BAD_REQUEST.value(), "social_linkage_required", "소셜 연동 해제가 필요합니다. "),
    EMAIL_MISMATCH(HttpStatus.CONFLICT.value(), "email_mismatch", "소셜 계정 이메일과 현재 이메일이 일치하지 않습니다. "),
    PROVIDER_MISMATCH(HttpStatus.CONFLICT.value(), "provider_mismatch", "소셜 계정의 제공자가 실제 연동된 계정과 일치하지 않습니다. "),
    NORMAL_USER_CANNOT_WITHDRAW(HttpStatus.CONFLICT.value(), "normal_user_cannot_withdraw", "일반 회원은 소셜 계정을 탈퇴할 수 없습니다.");

    private final int httpStatus;
    private final String code;
    private final String message;
}
