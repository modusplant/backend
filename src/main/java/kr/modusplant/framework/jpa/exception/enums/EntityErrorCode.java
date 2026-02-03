package kr.modusplant.framework.jpa.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EntityErrorCode implements ErrorCode {

    // Not Found
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND.value(), "not_found_comment", "댓글이 존재하지 않습니다"),
    NOT_FOUND_COMMENT_LIKE(HttpStatus.NOT_FOUND.value(), "not_found_comment_like", "댓글의 좋아요 값이 존재하지 않습니다"),

    NOT_FOUND_POST_ARCHIVE(HttpStatus.NOT_FOUND.value(), "not_found_post_archive", "게시글 아카이브의 값이 존재하지 않습니다"),
    NOT_FOUND_POST_BOOKMARK(HttpStatus.NOT_FOUND.value(), "not_found_post_bookmark", "게시글의 북마크 값이 존재하지 않습니다"),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND.value(), "not_found_post", "게시글이 존재하지 않습니다"),
    NOT_FOUND_POST_LIKE(HttpStatus.NOT_FOUND.value(), "not_found_post_like", "게시글의 좋아요 값이 존재하지 않습니다"),

    NOT_FOUND_PRIMARY_CATEGORY(HttpStatus.NOT_FOUND.value(), "not_found_primary_category", "1차 카테고리가 존재하지 않습니다"),
    NOT_FOUND_SECONDARY_CATEGORY(HttpStatus.NOT_FOUND.value(), "not_found_secondary_category", "2차 카테고리가 존재하지 않습니다"),

    NOT_FOUND_MEMBER_AUTH(HttpStatus.NOT_FOUND.value(), "not_found_member_auth", "사용자의 인증 정보가 존재하지 않습니다"),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND.value(), "not_found_member", "사용자의 계정이 존재하지 않습니다"),
    NOT_FOUND_MEMBER_PROFILE(HttpStatus.NOT_FOUND.value(), "not_found_member_profile", "사용자의 프로필 정보가 존재하지 않습니다"),
    NOT_FOUND_MEMBER_ROLE(HttpStatus.NOT_FOUND.value(), "not_found_member_role", "사용자의 역할 정보가 존재하지 않습니다"),
    NOT_FOUND_MEMBER_TERM(HttpStatus.NOT_FOUND.value(), "not_found_member_term", "사용자의 약관 정보가 존재하지 않습니다"),

    NOT_FOUND_TERM(HttpStatus.NOT_FOUND.value(), "not_found_term", "약관 정보가 존재하지 않습니다"),

    // Exists
    EXISTS_COMMENT(HttpStatus.NOT_FOUND.value(), "exists_comment", "댓글이 이미 존재합니다"),
    EXISTS_COMMENT_LIKE(HttpStatus.NOT_FOUND.value(), "exists_comment_like", "댓글의 좋아요가 이미 존재합니다"),

    EXISTS_POST_ARCHIVE(HttpStatus.NOT_FOUND.value(), "exists_post_archive", "게시글 아카이브 값이 이미 존재합니다"),
    EXISTS_POST_BOOKMARK(HttpStatus.NOT_FOUND.value(), "exists_post_bookmark", "게시글의 북마크 값이 이미 존재합니다"),
    EXISTS_POST(HttpStatus.NOT_FOUND.value(), "exists_post", "게시글이 이미 존재합니다"),
    EXISTS_POST_LIKE(HttpStatus.NOT_FOUND.value(), "exists_post_like", "게시글의 좋아요 값이 이미 존재합니다"),

    EXISTS_PRIMARY_CATEGORY(HttpStatus.NOT_FOUND.value(), "exists_primary_category", "1차 카테고리의 값이 이미 존재합니다"),
    EXISTS_SECONDARY_CATEGORY(HttpStatus.NOT_FOUND.value(), "exists_secondary_category", "2차 카테고리의 값이 이미 존재합니다"),

    EXISTS_MEMBER_AUTH(HttpStatus.NOT_FOUND.value(), "exists_member_auth", "사용자의 인증 정보가 이미 존재합니다"),
    EXISTS_MEMBER(HttpStatus.NOT_FOUND.value(), "exists_member", "사용자 계정의 정보가 이미 존재합니다"),
    EXISTS_MEMBER_PROFILE(HttpStatus.NOT_FOUND.value(), "exists_member_profile", "사용자의 프로필 정보가 이미 존재합니다"),
    EXISTS_MEMBER_ROLE(HttpStatus.NOT_FOUND.value(), "exists_member_role", "사용자의 역할 정보가 이미 존재합니다"),
    EXISTS_MEMBER_TERM(HttpStatus.NOT_FOUND.value(), "exists_member_term", "사용자의 약관 정보가 이미 존재합니다"),

    EXISTS_TERM(HttpStatus.NOT_FOUND.value(), "exists_term", "약관 정보가 이미 존재합니다");

    private final int httpStatus;
    private final String code;
    private final String message;
}
