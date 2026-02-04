package kr.modusplant.domains.term.domain.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TermErrorCode implements ErrorCode {
    EMPTY_TERM_ID(HttpStatus.BAD_REQUEST.value(), "empty_term_id", "약관 아이디가 비어 있습니다. "),
    EMPTY_TERM_NAME(HttpStatus.BAD_REQUEST.value(), "empty_term_name", "약관명이 비어 있습니다. "),
    EMPTY_TERM_CONTENT(HttpStatus.BAD_REQUEST.value(), "empty_term_content", "약관내용이 비어 있습니다. "),
    EMPTY_TERM_VERSION(HttpStatus.BAD_REQUEST.value(), "empty_term_version", "약관버전이 비어 있습니다. "),
    INVALID_TERM_VERSION(HttpStatus.BAD_REQUEST.value(), "invalid_term_version", "약관버전 형식이 잘못되었습니다. (ex v1.0.1) "),

    TERM_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "term_not_found", "존재하지 않는 약관입니다. "),
    NOT_FOUND_TERM_ID(HttpStatus.BAD_REQUEST.value(), "not_found_term_id", "약관 아이디를 찾을 수 없습니다. "),

    EMPTY_SITE_MEMBER_TERM_ID(HttpStatus.BAD_REQUEST.value(), "empty_site_member_term_id", "사이트 회원 약관 아이디가 비어 있습니다. "),
    EMPTY_AGREED_TERM_OF_USE_VERSION(HttpStatus.BAD_REQUEST.value(), "empty_agreed_terms_of_use_version", "동의한 이용약관 약관 버전이 비어 있습니다. "),
    EMPTY_AGREED_PRIVACY_POLICY_VERSION(HttpStatus.BAD_REQUEST.value(), "empty_agreed_privacy_policy_version", "동의한 개인정보처리방침 버전이 비어 있습니다. "),
    EMPTY_AGREED_AD_INFO_RECEIVING_VERSION(HttpStatus.BAD_REQUEST.value(), "empty_agreed_ad_info_receiving_version", "동의한 광고성 정보 수신 버전이 비어 있습니다. "),

    ALREADY_SITE_MEMBER_TERM(HttpStatus.BAD_REQUEST.value(), "already_site_member_term", "등록된 사이트 회원 약관이 존재합니다. "),

    SITE_MEMBER_TERM_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "site_member_term_not_found", "존재하지 않는 사이트 회원 약관입니다. "),
    SITE_MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "site_member_not_found", "존재하지 않는 회원 아이디입니다. "),


    ;

    private final int httpStatus;
    private final String code;
    private final String message;
}
