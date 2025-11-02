package kr.modusplant.domains.member.common.util.usecase.request;

import kr.modusplant.domains.member.usecase.request.MemberCommentLikeRequest;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_TARGET_COMMENT_PATH_STRING;
import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_TARGET_POST_ID_STRING;
import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_ID_UUID;

public interface MemberCommentLikeRequestTestUtils {
    MemberCommentLikeRequest testMemberCommentLikeRequest = new MemberCommentLikeRequest(TEST_MEMBER_ID_UUID, TEST_TARGET_POST_ID_STRING, TEST_TARGET_COMMENT_PATH_STRING);
}
