package kr.modusplant.shared.framework.jpa.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EntityErrorCode implements ErrorCode {
    // Not Found
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND.value(), "not_found_comment", "댓글이 존재하지 않습니다"),

    NOT_FOUND_POST_ARCHIVE(HttpStatus.NOT_FOUND.value(), "not_found_post_archive", "게시글 아카이브의 값이 존재하지 않습니다"),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND.value(), "not_found_post", "게시글이 존재하지 않습니다"),
    NOT_FOUND_POST_ID(HttpStatus.NOT_FOUND.value(), "not_found_post_id", "게시글 식별자가 존재하지 않습니다"),

    NOT_FOUND_PRIMARY_CATEGORY(HttpStatus.NOT_FOUND.value(), "not_found_primary_category", "1차 카테고리가 존재하지 않습니다"),
    NOT_FOUND_SECONDARY_CATEGORY(HttpStatus.NOT_FOUND.value(), "not_found_secondary_category", "2차 카테고리가 존재하지 않습니다"),

    NOT_FOUND_MEMBER_AUTH(HttpStatus.NOT_FOUND.value(), "not_found_member_auth", "사용자의 인증 정보가 존재하지 않습니다"),
    NOT_FOUND_MEMBER_ROLE(HttpStatus.NOT_FOUND.value(), "not_found_member_role", "사용자의 역할 정보가 존재하지 않습니다"),
    NOT_FOUND_MEMBER_TERM(HttpStatus.NOT_FOUND.value(), "not_found_member_term", "사용자의 약관 정보가 존재하지 않습니다"),

    NOT_FOUND_TERM(HttpStatus.NOT_FOUND.value(), "not_found_term", "약관 정보가 존재하지 않습니다"),
    NOT_FOUND_ACTOR(HttpStatus.NOT_FOUND.value(),"not_found_actor", "알림 행위자가 존재하지 않습니다"),

    EXISTS_TERM(HttpStatus.CONFLICT.value(), "exists_term", "약관 정보가 이미 존재합니다");

    private final int httpStatus;
    private final String code;
    private final String message;
}
