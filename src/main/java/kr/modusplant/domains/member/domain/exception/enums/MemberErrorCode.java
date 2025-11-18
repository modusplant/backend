package kr.modusplant.domains.member.domain.exception.enums;

import kr.modusplant.shared.exception.enums.supers.ResponseCode;
import kr.modusplant.shared.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ResponseCode {
    ALREADY_EXISTED_NICKNAME(HttpStatus.BAD_REQUEST, "already_existed_nickname", "이미 동일한 닉네임이 존재합니다. "),
    EMPTY_MEMBER_BIRTH_DATE(HttpStatus.BAD_REQUEST, "empty_member_birth_date", "회원 생일이 비어 있습니다. "),
    EMPTY_MEMBER_ID(HttpStatus.BAD_REQUEST, "empty_member_id", "회원 아이디가 비어 있습니다. "),
    EMPTY_MEMBER_NICKNAME(HttpStatus.BAD_REQUEST, "empty_member_nickname", "회원 닉네임이 비어 있습니다. "),
    EMPTY_MEMBER_STATUS(HttpStatus.BAD_REQUEST, "empty_member_status", "회원 상태가 비어 있습니다. "),
    EMPTY_MEMBER_PROFILE_IMAGE(HttpStatus.BAD_REQUEST, "empty_member_profile_image", "회원 프로필 이미지가 비어 있습니다. "),
    EMPTY_MEMBER_PROFILE_IMAGE_BYTES(HttpStatus.BAD_REQUEST, "empty_member_profile_image_bytes", "회원 프로필 이미지 바이트 값이 비어 있습니다. "),
    EMPTY_MEMBER_PROFILE_IMAGE_PATH(HttpStatus.BAD_REQUEST, "empty_member_profile_image_path", "회원 프로필 이미지 경로가 비어 있습니다. "),
    EMPTY_MEMBER_PROFILE_INTRODUCTION(HttpStatus.BAD_REQUEST, "empty_member_profile_introduction", "회원 프로필 소개가 비어 있습니다. "),
    EMPTY_TARGET_COMMENT_PATH(HttpStatus.BAD_REQUEST, "empty_target_path", "대상 댓글 경로가 비어 있습니다. "),
    EMPTY_TARGET_POST_ID(HttpStatus.BAD_REQUEST, "empty_target_post_id", "대상 게시글 아이디가 비어 있습니다. "),
    MEMBER_PROFILE_INTRODUCTION_OVER_LENGTH(HttpStatus.BAD_REQUEST, "member_profile_introduction_over_length", "회원 프로필 소개가 허용되는 길이를 초과하였습니다. "),
    NOT_ACCESSIBLE_POST_BOOKMARK(HttpStatus.BAD_REQUEST, "not_accessible_post_bookmark", "대상 게시글에 대한 북마크 기능을 이용할 수 없습니다. "),
    NOT_ACCESSIBLE_POST_LIKE(HttpStatus.BAD_REQUEST, "not_accessible_post_like", "대상 게시글에 대한 좋아요 기능을 이용할 수 없습니다. "),
    NOT_FOUND_MEMBER_ID(HttpStatus.BAD_REQUEST, "not_found_member_id", "회원 아이디를 찾을 수 없습니다. "),
    NOT_FOUND_TARGET_POST_ID(HttpStatus.BAD_REQUEST, "not_found_target_post_id", "대상 게시글 아이디를 찾을 수 없습니다. "),
    NOT_FOUND_TARGET_COMMENT_ID(HttpStatus.BAD_REQUEST, "not_found_target_comment_id", "대상 댓글 아이디를 찾을 수 없습니다. ");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public boolean isSuccess() {
        return false;
    }
}
