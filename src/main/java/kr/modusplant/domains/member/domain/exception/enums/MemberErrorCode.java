package kr.modusplant.domains.member.domain.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    EMPTY_MEMBER_BIRTH_DATE(HttpStatus.BAD_REQUEST.value(), "empty_member_birth_date", "회원 생일이 비어 있습니다. "),
    EMPTY_MEMBER_ID(HttpStatus.BAD_REQUEST.value(), "empty_member_id", "회원 아이디가 비어 있습니다. "),
    EMPTY_MEMBER_STATUS(HttpStatus.BAD_REQUEST.value(), "empty_member_status", "회원 상태가 비어 있습니다. "),
    EMPTY_MEMBER_PROFILE_IMAGE(HttpStatus.BAD_REQUEST.value(), "empty_member_profile_image", "회원 프로필 이미지가 비어 있습니다. "),
    EMPTY_MEMBER_PROFILE_IMAGE_BYTES(HttpStatus.BAD_REQUEST.value(), "empty_member_profile_image_bytes", "회원 프로필 이미지 바이트 값이 비어 있습니다. "),
    EMPTY_MEMBER_PROFILE_IMAGE_PATH(HttpStatus.BAD_REQUEST.value(), "empty_member_profile_image_path", "회원 프로필 이미지 경로가 비어 있습니다. "),
    EMPTY_MEMBER_PROFILE_INTRODUCTION(HttpStatus.BAD_REQUEST.value(), "empty_member_profile_introduction", "회원 프로필 소개가 비어 있습니다. "),
    EMPTY_REPORT_CONTENT(HttpStatus.BAD_REQUEST.value(), "empty_report_content", "보고서 컨텐츠가 비어 있습니다. "),
    EMPTY_REPORT_IMAGE_BYTES(HttpStatus.BAD_REQUEST.value(), "empty_report_image_bytes", "보고서 이미지 바이트 값이 비어 있습니다. "),
    EMPTY_REPORT_IMAGE_PATH(HttpStatus.BAD_REQUEST.value(), "empty_report_image_path", "보고서 이미지 경로가 비어 있습니다. "),
    EMPTY_REPORT_TITLE(HttpStatus.BAD_REQUEST.value(), "empty_report_title", "보고서 제목이 비어 있습니다. "),
    EMPTY_TARGET_COMMENT_PATH(HttpStatus.BAD_REQUEST.value(), "empty_target_path", "대상 댓글 경로가 비어 있습니다. "),
    EMPTY_TARGET_POST_ID(HttpStatus.BAD_REQUEST.value(), "empty_target_post_id", "대상 게시글 아이디가 비어 있습니다. "),
    INCORRECT_MEMBER_ID(HttpStatus.BAD_REQUEST.value(), "incorrect_member_id", "올바르지 않은 회원 ID를 사용하고 있습니다. "),
    INVALID_MEMBER_PROFILE_IMAGE_PATH(HttpStatus.BAD_REQUEST.value(), "invalid_member_profile_image_path", "회원 프로필 이미지 경로의 서식이 올바르지 않습니다. "),
    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST.value(), "invalid_member_id", "사용자 식별자의 서식이 올바르지 않습니다. "),
    INVALID_REPORT_IMAGE_PATH(HttpStatus.BAD_REQUEST.value(), "invalid_report_image_path", "보고서 이미지 경로의 서식이 올바르지 않습니다. "),
    INVALID_TARGET_COMMENT_PATH(HttpStatus.BAD_REQUEST.value(), "invalid_target_comment_path", "목표로 하는 댓글 경로의 서식이 올바르지 않습니다. "),
    INVALID_TARGET_POST_ID(HttpStatus.BAD_REQUEST.value(), "invalid_target_post_id", "목표로 하는 게시글 식별자의 서식이 올바르지 않습니다. "),
    MEMBER_PROFILE_INTRODUCTION_OVER_LENGTH(HttpStatus.BAD_REQUEST.value(), "member_profile_introduction_over_length", "회원 프로필 소개가 허용되는 길이를 초과하였습니다. "),
    REPORT_CONTENT_OVER_LENGTH(HttpStatus.BAD_REQUEST.value(), "report_content_over_length", "보고서 컨텐츠가 허용되는 길이를 초과하였습니다. "),
    REPORT_TITLE_OVER_LENGTH(HttpStatus.BAD_REQUEST.value(), "report_title_over_length", "보고서 제목이 허용되는 길이를 초과하였습니다. "),
    NOT_ACCESSIBLE_POST_BOOKMARK(HttpStatus.BAD_REQUEST.value(), "not_accessible_post_bookmark", "대상 게시글에 대한 북마크 기능을 이용할 수 없습니다. "),
    NOT_ACCESSIBLE_POST_LIKE(HttpStatus.BAD_REQUEST.value(), "not_accessible_post_like", "대상 게시글에 대한 좋아요 기능을 이용할 수 없습니다. "),
    NOT_ACCESSIBLE_POST_REPORT_FOR_ABUSE(HttpStatus.BAD_REQUEST.value(), "not_accessible_post_report_for_abuse", "대상 게시글에 대한 신고 기능을 이용할 수 없습니다. "),
    NOT_FOUND_MEMBER_ID(HttpStatus.BAD_REQUEST.value(), "not_found_member_id", "회원 아이디를 찾을 수 없습니다. "),
    NOT_FOUND_TARGET_POST_ID(HttpStatus.BAD_REQUEST.value(), "not_found_target_post_id", "대상 게시글 아이디를 찾을 수 없습니다. "),
    NOT_FOUND_TARGET_COMMENT_ID(HttpStatus.BAD_REQUEST.value(), "not_found_target_comment_id", "대상 댓글 아이디를 찾을 수 없습니다. ");

    private final int httpStatus;
    private final String code;
    private final String message;
}
