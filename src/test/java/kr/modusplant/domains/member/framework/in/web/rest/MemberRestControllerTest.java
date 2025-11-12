package kr.modusplant.domains.member.framework.in.web.rest;

import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import kr.modusplant.framework.out.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

import static kr.modusplant.domains.member.common.util.usecase.record.MemberCancelPostBookmarkRecordTestUtils.testMemberCancelPostBookmarkRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCheckNicknameRecordTestUtils.testMemberCheckNicknameRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCommentLikeRecordTestUtils.testMemberCommentLikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberCommentUnlikeRecordTestUtils.testMemberCommentUnlikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostBookmarkRecordTestUtils.testMemberPostBookmarkRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostLikeRecordTestUtils.testMemberPostLikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberPostUnlikeRecordTestUtils.testMemberPostUnlikeRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileGetRecordTestUtils.testMemberProfileGetRecord;
import static kr.modusplant.domains.member.common.util.usecase.record.MemberProfileOverrideRecordTestUtils.testMemberProfileOverrideRecord;
import static kr.modusplant.domains.member.common.util.usecase.request.MemberRegisterRequestTestUtils.testMemberRegisterRequest;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberProfileResponseTestUtils.testMemberProfileResponse;
import static kr.modusplant.domains.member.common.util.usecase.response.MemberResponseTestUtils.testMemberResponse;
import static kr.modusplant.infrastructure.config.jackson.TestJacksonConfig.objectMapper;
import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_INTRODUCTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class MemberRestControllerTest implements MemberTestUtils {
    @SuppressWarnings({"unused", "InstantiationOfUtilityClass"})
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final MemberController memberController = Mockito.mock(MemberController.class);
    private final MemberRestController memberRestController = new MemberRestController(memberController);

    @Test
    @DisplayName("register로 응답 반환")
    void testRegister_givenValidRequest_willReturnResponse() {
        // given
        given(memberController.register(testMemberRegisterRequest)).willReturn(testMemberResponse);

        // when
        ResponseEntity<DataResponse<MemberResponse>> memberResponseEntity = memberRestController.registerMember(testMemberRegisterRequest);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testMemberResponse).toString());
    }

    @Test
    @DisplayName("checkExistedMemberNickname으로 응답 반환")
    void testCheckExistedMemberNickname_givenValidRequest_willReturnResponse() {
        // given
        given(memberController.checkExistedNickname(testMemberCheckNicknameRecord)).willReturn(true);

        // when
        ResponseEntity<DataResponse<Map<String, Boolean>>> isExistedMemberNickname =
                memberRestController.checkExistedMemberNickname(MEMBER_BASIC_USER_NICKNAME);

        // then
        assertThat(isExistedMemberNickname.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(isExistedMemberNickname.getBody().getData().toString()).isEqualTo(Map.of("isNicknameExisted", true).toString());
    }

    @Test
    @DisplayName("getMemberProfile로 응답 반환")
    void testGetMemberProfile_givenValidId_willReturnResponse() throws IOException {
        // given
        given(memberController.getProfile(testMemberProfileGetRecord)).willReturn(testMemberProfileResponse);

        // when
        ResponseEntity<DataResponse<MemberProfileResponse>> memberResponseEntity = memberRestController.getMemberProfile(MEMBER_BASIC_USER_UUID);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testMemberProfileResponse).toString());
    }

    @Test
    @DisplayName("overrideMemberProfile로 응답 반환")
    void testOverrideMemberProfile_givenValidParameters_willReturnResponse() throws IOException {
        // given
        given(memberController.overrideProfile(testMemberProfileOverrideRecord)).willReturn(testMemberProfileResponse);

        // when
        ResponseEntity<DataResponse<MemberProfileResponse>> memberResponseEntity = memberRestController.overrideMemberProfile(MEMBER_BASIC_USER_UUID, MEMBER_PROFILE_BASIC_USER_IMAGE, MEMBER_PROFILE_BASIC_USER_INTRODUCTION, MEMBER_BASIC_USER_NICKNAME);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testMemberProfileResponse).toString());
    }

    @Test
    @DisplayName("likeCommunicationPost로 응답 반환")
    void testLikeCommunicationPost_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).likePost(testMemberPostLikeRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.likeCommunicationPost(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("unlikeCommunicationPost로 응답 반환")
    void testUnlikeCommunicationPost_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).unlikePost(testMemberPostUnlikeRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.unlikeCommunicationPost(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("bookmarkCommunicationPost로 응답 반환")
    void testBookmarkCommunicationPost_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).bookmarkPost(testMemberPostBookmarkRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.bookmarkCommunicationPost(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("cancelCommunicationPostBookmark로 응답 반환")
    void testCancelCommunicationPostBookmark_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).cancelPostBookmark(testMemberCancelPostBookmarkRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.cancelCommunicationPostBookmark(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("likeCommunicationComment로 응답 반환")
    void testLikeCommunicationComment_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).likeComment(testMemberCommentLikeRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.likeCommunicationComment(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("unlikeCommunicationComment로 응답 반환")
    void testUnlikeCommunicationComment_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).unlikeComment(testMemberCommentUnlikeRecord);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.unlikeCommunicationComment(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }
}