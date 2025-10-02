package kr.modusplant.domains.member.framework.in.web.rest;

import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.common.util.adapter.response.MemberResponseTestUtils;
import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import kr.modusplant.framework.out.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_MEMBER_NICKNAME_STRING;
import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_ID_UUID;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetCommentIdTestUtils.testTargetCommentId;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetCommentPathTestUtils.testTargetCommentPath;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetPostIdTestUtils.testTargetPostId;
import static kr.modusplant.infrastructure.config.jackson.TestJacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class MemberRestControllerTest implements MemberTestUtils, MemberResponseTestUtils {
    @SuppressWarnings({"unused", "InstantiationOfUtilityClass"})
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final MemberController memberController = Mockito.mock(MemberController.class);
    private final MemberRestController memberRestController = new MemberRestController(memberController);

    @Test
    @DisplayName("registerMember로 응답 반환")
    void testRegisterMember_givenValidNickname_willReturnResponse() {
        // given
        given(memberController.register(testMemberNickname)).willReturn(testMemberResponse);

        // when
        ResponseEntity<DataResponse<MemberResponse>> memberResponseEntity = memberRestController.registerMember(TEST_MEMBER_NICKNAME_STRING);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testMemberResponse).toString());
    }

    @Test
    @DisplayName("updateMemberNickname으로 응답 반환")
    void testUpdateMemberNickname_givenValidRequest_willReturnResponse() {
        // given
        given(memberController.updateNickname(testMemberId, testMemberNickname)).willReturn(testMemberResponse);

        // when
        ResponseEntity<DataResponse<MemberResponse>> memberResponseEntity = memberRestController.updateMemberNickname(TEST_MEMBER_ID_UUID, TEST_MEMBER_NICKNAME_STRING);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testMemberResponse).toString());
    }

    @Test
    @DisplayName("likeCommunicationPost로 응답 반환")
    void testLikeCommunicationPost_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).likePost(testMemberId, testTargetPostId);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.likeCommunicationPost(testMemberId.getValue(), testTargetPostId.getValue());

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("unlikeCommunicationPost로 응답 반환")
    void testUnlikeCommunicationPost_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).unlikePost(testMemberId, testTargetPostId);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.unlikeCommunicationPost(testMemberId.getValue(), testTargetPostId.getValue());

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("likeCommunicationComment로 응답 반환")
    void testLikeCommunicationComment_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).likeComment(testMemberId, testTargetCommentId);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.likeCommunicationComment(testMemberId.getValue(), testTargetPostId.getValue(), testTargetCommentPath.getValue());

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("unlikeCommunicationComment로 응답 반환")
    void testUnlikeCommunicationComment_givenValidRequest_willReturnResponse() {
        // given
        willDoNothing().given(memberController).unlikeComment(testMemberId, testTargetCommentId);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.unlikeCommunicationComment(testMemberId.getValue(), testTargetPostId.getValue(), testTargetCommentPath.getValue());

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }
}