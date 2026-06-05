package kr.modusplant.domains.member.domain.exception.enums;

import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    EXISTS_COMMENT_ABUSE_REPORT(HttpStatus.CONFLICT.value(), "exists_comment_abuse_report", "댓글 신고가 이미 존재합니다"),
    EXISTS_COMMENT_ABUSE_REPORT_BLINDED(HttpStatus.CONFLICT.value(), "exists_comment_abuse_report_blinded", "댓글 신고가 이미 수리되었습니다. "),
    EXISTS_COMMENT_ABUSE_REPORT_DISMISSED(HttpStatus.CONFLICT.value(), "exists_comment_abuse_report_dismissed", "댓글 신고가 이미 반려되었습니다. "),
    EXISTS_POST_ABUSE_REPORT(HttpStatus.CONFLICT.value(), "exists_post_abuse_report", "게시글 신고가 이미 존재합니다"),
    EXISTS_POST_ABUSE_REPORT_BLINDED(HttpStatus.CONFLICT.value(), "exists_post_abuse_report_blinded", "게시글 신고가 이미 수리되었습니다. "),
    EXISTS_POST_ABUSE_REPORT_DISMISSED(HttpStatus.CONFLICT.value(), "exists_post_abuse_report_dismissed", "게시글 신고가 이미 반려되었습니다. "),
    EXISTS_PROPOSAL_OR_BUG_REPORT_CHECKED(HttpStatus.CONFLICT.value(), "exists_proposal_or_bug_report_checked", "건의 및 버그 제보가 이미 확인되었습니다. "),

    EMPTY_MEMBER_ID(HttpStatus.BAD_REQUEST.value(), "empty_member_id", "회원 아이디가 비어 있습니다. "),
    EMPTY_MEMBER_STATUS(HttpStatus.BAD_REQUEST.value(), "empty_member_status", "회원 상태가 비어 있습니다. "),
    EMPTY_MEMBER_PROFILE_IMAGE(HttpStatus.BAD_REQUEST.value(), "empty_member_profile_image", "회원 프로필 이미지가 비어 있습니다. "),
    EMPTY_MEMBER_PROFILE_IMAGE_BYTES(HttpStatus.BAD_REQUEST.value(), "empty_member_profile_image_bytes", "회원 프로필 이미지 바이트 값이 비어 있습니다. "),
    EMPTY_MEMBER_PROFILE_IMAGE_PATH(HttpStatus.BAD_REQUEST.value(), "empty_member_profile_image_path", "회원 프로필 이미지 경로가 비어 있습니다. "),
    EMPTY_MEMBER_PROFILE_INTRODUCTION(HttpStatus.BAD_REQUEST.value(), "empty_member_profile_introduction", "회원 프로필 소개가 비어 있습니다. "),
    EMPTY_REPORT_CONTENT(HttpStatus.BAD_REQUEST.value(), "empty_report_content", "보고서 컨텐츠가 비어 있습니다. "),
    EMPTY_REPORT_ID(HttpStatus.BAD_REQUEST.value(), "empty_report_id", "보고서 식별자가 비어 있습니다. "),
    EMPTY_REPORT_IMAGE(HttpStatus.BAD_REQUEST.value(), "empty_report_image", "보고서 이미지가 비어 있습니다. "),
    EMPTY_REPORT_IMAGE_BYTES(HttpStatus.BAD_REQUEST.value(), "empty_report_image_bytes", "보고서 이미지 바이트 값이 비어 있습니다. "),
    EMPTY_REPORT_IMAGE_FILE_NAME(HttpStatus.BAD_REQUEST.value(), "empty_report_image_file_name", "보고서 이미지 파일명이 비어 있습니다. "),
    EMPTY_REPORT_IMAGE_NUMBER(HttpStatus.BAD_REQUEST.value(), "empty_report_image_number", "보고서 이미지 개수가 비어 있습니다. "),
    EMPTY_REPORT_IMAGE_PATH(HttpStatus.BAD_REQUEST.value(), "empty_report_image_path", "보고서 이미지 경로가 비어 있습니다. "),
    EMPTY_REPORT_PAGE_SIZE(HttpStatus.BAD_REQUEST.value(), "empty_report_page_size", "보고서 페이지 크기가 비어 있습니다. "),
    EMPTY_REPORT_TIME(HttpStatus.BAD_REQUEST.value(), "empty_report_time", "보고 시각이 비어 있습니다. "),
    EMPTY_REPORT_TITLE(HttpStatus.BAD_REQUEST.value(), "empty_report_title", "보고서 제목이 비어 있습니다. "),
    EMPTY_ACTIVITY_SUBJECT_COMMENT_PATH(HttpStatus.BAD_REQUEST.value(), "empty_activity_subject_comment_path", "대상 댓글 경로가 비어 있습니다. "),
    EMPTY_ACTIVITY_SUBJECT_POST_ID(HttpStatus.BAD_REQUEST.value(), "empty_activity_subject_post_id", "대상 게시글 아이디가 비어 있습니다. "),

    INVALID_MEMBER_PROFILE_IMAGE_PATH(HttpStatus.BAD_REQUEST.value(), "invalid_member_profile_image_path", "회원 프로필 이미지 경로의 서식이 올바르지 않습니다. "),
    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST.value(), "invalid_member_id", "사용자 식별자의 서식이 올바르지 않습니다. "),
    INVALID_REPORT_ID(HttpStatus.BAD_REQUEST.value(), "invalid_report_id", "보고서 식별자의 서식이 올바르지 않습니다. "),
    INVALID_REPORT_IMAGE_FILE_NAME(HttpStatus.BAD_REQUEST.value(), "invalid_report_image_file_name", "보고서 이미지 파일명의 서식이 올바르지 않습니다. "),
    INVALID_REPORT_IMAGE_NAME(HttpStatus.BAD_REQUEST.value(), "invalid_report_image_name", "보고서 이미지의 이름이 올바르지 않습니다. "),
    INVALID_REPORT_IMAGE_PATH(HttpStatus.BAD_REQUEST.value(), "invalid_report_image_path", "보고서 이미지 경로의 서식이 올바르지 않습니다. "),
    INVALID_ACTIVITY_SUBJECT_COMMENT_PATH(HttpStatus.BAD_REQUEST.value(), "invalid_activity_subject_comment_path", "대상 댓글 경로의 서식이 올바르지 않습니다. "),
    INVALID_ACTIVITY_SUBJECT_POST_ID(HttpStatus.BAD_REQUEST.value(), "invalid_activity_subject_post_id", "대상 게시글 식별자의 서식이 올바르지 않습니다. "),

    MEMBER_PROFILE_INTRODUCTION_OVER_LENGTH(HttpStatus.BAD_REQUEST.value(), "member_profile_introduction_over_length", "회원 프로필 소개가 허용되는 길이를 초과하였습니다. "),
    MEMBER_WITHDRAW_OPINION_OVER_LENGTH(HttpStatus.BAD_REQUEST.value(), "member_withdraw_opinion_over_length", "회원 탈퇴 관련 의견이 허용되는 길이를 초과하였습니다. "),
    REPORT_CONTENT_OVER_LENGTH(HttpStatus.BAD_REQUEST.value(), "report_content_over_length", "보고서 컨텐츠가 허용되는 길이를 초과하였습니다. "),
    REPORT_TITLE_OVER_LENGTH(HttpStatus.BAD_REQUEST.value(), "report_title_over_length", "보고서 제목이 허용되는 길이를 초과하였습니다. "),

    MISMATCHED_AUTH_INFO(HttpStatus.BAD_REQUEST.value(), "mismatched_auth_info", "인증 정보가 올바르지 않습니다. "),
    MISMATCHED_REPORT_IMAGE_SIZE(HttpStatus.BAD_REQUEST.value(), "mismatched_report_image_size", "보고서 이미지 개수가 올바르지 않습니다. "),

    NOT_ACCESSIBLE_POST_BOOKMARK(HttpStatus.BAD_REQUEST.value(), "not_accessible_post_bookmark", "대상 게시글에 대한 북마크 기능을 이용할 수 없습니다. "),
    NOT_ACCESSIBLE_POST_LIKE(HttpStatus.BAD_REQUEST.value(), "not_accessible_post_like", "대상 게시글에 대한 좋아요 기능을 이용할 수 없습니다. "),
    NOT_ACCESSIBLE_POST_REPORT_FOR_ABUSE(HttpStatus.BAD_REQUEST.value(), "not_accessible_post_report_for_abuse", "대상 게시글에 대한 신고 기능을 이용할 수 없습니다. "),

    NOT_FOUND_ACTIVITY_SUBJECT_POST_ID(HttpStatus.NOT_FOUND.value(), "not_found_activity_subject_post_id", "대상 게시글 아이디를 찾을 수 없습니다. "),
    NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID(HttpStatus.NOT_FOUND.value(), "not_found_activity_subject_comment_id", "대상 댓글 아이디를 찾을 수 없습니다. "),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND.value(), "not_found_member", "회원을 찾을 수 없습니다. "),
    NOT_FOUND_MEMBER_ID(HttpStatus.NOT_FOUND.value(), "not_found_member_id", "회원 아이디를 찾을 수 없습니다. "),
    NOT_FOUND_MEMBER_PROFILE(HttpStatus.NOT_FOUND.value(), "not_found_member_profile", "회원 프로필을 찾을 수 없습니다. "),
    NOT_FOUND_COMMENT_ABUSE_REPORT(HttpStatus.NOT_FOUND.value(), "not_found_comment_abuse_report", "댓글 신고를 찾을 수 없습니다. "),
    NOT_FOUND_COMMENT_ABUSE_REPORT_DASHBOARD(HttpStatus.NOT_FOUND.value(), "not_found_comment_abuse_report_dashboard", "댓글 신고 대시보드를 찾을 수 없습니다. "),
    NOT_FOUND_POST_ABUSE_REPORT(HttpStatus.NOT_FOUND.value(), "not_found_post_abuse_report", "게시글 신고를 찾을 수 없습니다. "),
    NOT_FOUND_POST_ABUSE_REPORT_DASHBOARD(HttpStatus.NOT_FOUND.value(), "not_found_post_abuse_report_dashboard", "게시글 신고 대시보드를 찾을 수 없습니다. "),
    NOT_FOUND_PROPOSAL_OR_BUG_REPORT(HttpStatus.NOT_FOUND.value(), "not_found_proposal_or_bug_report", "건의 및 버그 제보를 찾을 수 없습니다. "),
    NOT_FOUND_PROPOSAL_OR_BUG_REPORT_CHECKED(HttpStatus.NOT_FOUND.value(), "not_found_proposal_or_bug_report_checked", "확인된 건의 및 버그 제보를 찾을 수 없습니다. "),
    NOT_FOUND_REPORT_ID(HttpStatus.NOT_FOUND.value(), "not_found_report_id", "보고서 식별자를 찾을 수 없습니다. "),

    NOT_FOUND_PROPOSAL_OR_BUG_REPORT_IMAGE_NUMBER(HttpStatus.INTERNAL_SERVER_ERROR.value(), "not_found_proposal_or_bug_image_number", "건의 및 버그 제보 이미지 개수를 찾을 수 없습니다. "),

    REPORT_PAGE_SIZE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST.value(), "report_page_size_out_of_range", "보고서 페이지 크기가 올바른 값의 범위를 벗어났습니다. "),

    PROPOSAL_OR_BUG_REPORT_IMAGE_NUMBER_OUT_OF_RANGE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "proposal_or_bug_report_image_number_out_of_range", "건의 및 버그 제보 이미지 개수가 올바른 값의 범위를 벗어났습니다. "),

    REPORT_TIME_ON_FUTURE(HttpStatus.BAD_REQUEST.value(), "report_time_on_future", "보고 시각이 미래입니다. "),
    ;

    private final int httpStatus;
    private final String code;
    private final String message;
}
