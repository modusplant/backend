package kr.modusplant.domains.member.framework.in.web.rest;

import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.adapter.response.MemberResponse;
import kr.modusplant.domains.member.test.utils.adapter.MemberRequestTestUtils;
import kr.modusplant.domains.member.test.utils.adapter.MemberResponseTestUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberTestUtils;
import kr.modusplant.framework.out.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static kr.modusplant.framework.out.config.jackson.TestJacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class MemberRestControllerTest implements MemberTestUtils, MemberRequestTestUtils, MemberResponseTestUtils {
    @SuppressWarnings({"unused", "InstantiationOfUtilityClass"})
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final MemberController memberController = Mockito.mock(MemberController.class);
    private final MemberRestController memberRestController = new MemberRestController(memberController);

    private final String testPostId = "01K41TWHC1WNYAB9YKC8Q29GGM";

    @Test
    @DisplayName("registerMember로 응답 반환")
    void callRegisterMember_withValidRequest_returnsResponse() {
        // given
        given(memberController.register(testMemberRegisterRequest)).willReturn(testMemberResponse);

        // when
        ResponseEntity<DataResponse<MemberResponse>> memberResponseEntity = memberRestController.registerMember(testMemberRegisterRequest);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testMemberResponse).toString());
    }

    @Test
    @DisplayName("updateMemberNickname으로 응답 반환")
    void callUpdateMemberNickname_withValidRequest_returnsResponse() {
        // given
        given(memberController.updateNickname(testMemberNicknameUpdateRequest)).willReturn(testMemberResponse);

        // when
        ResponseEntity<DataResponse<MemberResponse>> memberResponseEntity = memberRestController.updateMemberNickname(testMemberNicknameUpdateRequest);

        // then
        assertThat(memberResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(memberResponseEntity.getBody().toString()).isEqualTo(DataResponse.ok(testMemberResponse).toString());
    }

    @Test
    @DisplayName("likeCommunicationPost로 응답 반환")
    void callLikeCommunicationPost_withValidRequest_returnsResponse() {
        // given
        willDoNothing().given(memberController).likePost(testMemberId.getValue(), testPostId);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.likeCommunicationPost(testMemberId.getValue(), testPostId);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }

    @Test
    @DisplayName("unlikeCommunicationPost로 응답 반환")
    void callUnlikeCommunicationPost_withValidRequest_returnsResponse() {
        // given
        willDoNothing().given(memberController).unlikePost(testMemberId.getValue(), testPostId);

        // when
        ResponseEntity<DataResponse<Void>> responseEntity = memberRestController.unlikeCommunicationPost(testMemberId.getValue(), testPostId);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().toString()).isEqualTo(DataResponse.ok().toString());
    }
}