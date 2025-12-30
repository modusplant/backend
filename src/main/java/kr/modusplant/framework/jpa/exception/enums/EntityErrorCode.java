package kr.modusplant.framework.jpa.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EntityErrorCode implements ResponseCode {

    // Not Found
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "comment_not_found", "댓글이 존재하지 않습니다"),
    NOT_FOUND_COMMENT_LIKE(HttpStatus.NOT_FOUND, "member_not_found", "댓글의 좋아요가 존재하지 않습니다"),

    NOT_FOUND_POST_ARCHIVE(HttpStatus.NOT_FOUND, "member_not_found", "사용자의 계정이 존재하지 않습니다"),
    NOT_FOUND_POST_BOOKMARK(HttpStatus.NOT_FOUND, "member_not_found", "사용자의 계정이 존재하지 않습니다"),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "post_not_found", "게시글을 찾을 수 없습니다"),
    NOT_FOUND_POST_LIKE(HttpStatus.NOT_FOUND, "post_not_found", "게시글을 찾을 수 없습니다"),

    NOT_FOUND_PRIMARY_CATEGORY(HttpStatus.NOT_FOUND, "post_not_found", "게시글을 찾을 수 없습니다"),
    NOT_FOUND_SECONDARY_CATEGORY(HttpStatus.NOT_FOUND, "post_not_found", "게시글을 찾을 수 없습니다"),

    NOT_FOUND_MEMBER_AUTH(HttpStatus.NOT_FOUND, "member_not_found", "사용자의 계정이 존재하지 않습니다"),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "member_not_found", "사용자의 계정이 존재하지 않습니다"),
    NOT_FOUND_MEMBER_PROFILE(HttpStatus.NOT_FOUND, "member_profile_not_found", "사용자의 프로필 정보가 존재하지 않습니다"),
    NOT_FOUND_MEMBER_ROLE(HttpStatus.NOT_FOUND, "member_role_not_found", "사용자의 역할 정보가 존재하지 않습니다"),
    NOT_FOUND_MEMBER_TERM(HttpStatus.NOT_FOUND, "member_role_not_found", "사용자의 역할 정보가 존재하지 않습니다"),

    NOT_FOUND_TERM(HttpStatus.NOT_FOUND, "member_role_not_found", "사용자의 역할 정보가 존재하지 않습니다"),

    // Exists
    EXISTS_COMMENT(HttpStatus.NOT_FOUND, "comment_not_found", "댓글이 존재하지 않습니다"),
    EXISTS_COMMENT_LIKE(HttpStatus.NOT_FOUND, "member_not_found", "댓글의 좋아요가 존재하지 않습니다"),

    EXISTS_POST_ARCHIVE(HttpStatus.NOT_FOUND, "member_not_found", "사용자의 계정이 존재하지 않습니다"),
    EXISTS_POST_BOOKMARK(HttpStatus.NOT_FOUND, "member_not_found", "사용자의 계정이 존재하지 않습니다"),
    EXISTS_POST(HttpStatus.NOT_FOUND, "post_not_found", "게시글을 찾을 수 없습니다"),
    EXISTS_POST_LIKE(HttpStatus.NOT_FOUND, "post_not_found", "게시글을 찾을 수 없습니다"),

    EXISTS_PRIMARY_CATEGORY(HttpStatus.NOT_FOUND, "post_not_found", "게시글을 찾을 수 없습니다"),
    EXISTS_SECONDARY_CATEGORY(HttpStatus.NOT_FOUND, "post_not_found", "게시글을 찾을 수 없습니다"),

    EXISTS_MEMBER_AUTH(HttpStatus.NOT_FOUND, "member_not_found", "사용자의 계정이 존재하지 않습니다"),
    EXISTS_MEMBER(HttpStatus.NOT_FOUND, "member_not_found", "사용자의 계정이 존재하지 않습니다"),
    EXISTS_MEMBER_PROFILE(HttpStatus.NOT_FOUND, "member_profile_not_found", "사용자의 프로필 정보가 존재하지 않습니다"),
    EXISTS_MEMBER_ROLE(HttpStatus.NOT_FOUND, "member_role_not_found", "사용자의 역할 정보가 존재하지 않습니다"),
    EXISTS_MEMBER_TERM(HttpStatus.NOT_FOUND, "member_role_not_found", "사용자의 역할 정보가 존재하지 않습니다"),

    EXISTS_TERM(HttpStatus.NOT_FOUND, "member_role_not_found", "사용자의 역할 정보가 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
