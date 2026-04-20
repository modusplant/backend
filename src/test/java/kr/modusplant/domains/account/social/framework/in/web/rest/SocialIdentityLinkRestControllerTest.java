package kr.modusplant.domains.account.social.framework.in.web.rest;

import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityController;
import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityLinkController;
import kr.modusplant.domains.account.social.common.util.usecase.request.SocialAuthRequestTestUtils;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.framework.jackson.holder.ObjectMapperHolder;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.common.util.SiteMemberUserDetailsTestUtils;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_CODE;
import static kr.modusplant.infrastructure.config.jackson.TestJacksonConfig.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SocialIdentityLinkRestControllerTest implements SocialAuthRequestTestUtils, SiteMemberUserDetailsTestUtils {
    private final ObjectMapperHolder objectMapperHolder = new ObjectMapperHolder(objectMapper());
    private final SocialIdentityController socialIdentityController = mock(SocialIdentityController.class);
    private final SocialIdentityLinkController socialIdentityLinkController = mock(SocialIdentityLinkController.class);
    private final SocialIdentityLinkRestController socialIdentityLinkRestController = new SocialIdentityLinkRestController(
            socialIdentityController, socialIdentityLinkController
    );

    @Test
    @DisplayName("소셜 연동 성공 시 응답을 반환한다.")
    void testLinkSocialAccount_givenCode_willReturnSuccessResponse() {
        // given
        DefaultUserDetails userDetails = testDefaultMemberUserDetailsBuilder.build();
        String socialAccessToken = "social-access-token";
        given(socialIdentityController.issueSocialAccessToken(SocialProvider.KAKAO,TEST_SOCIAL_KAKAO_CODE)).willReturn(socialAccessToken);
        willDoNothing().given(socialIdentityLinkController).linkSocialAccount(userDetails.getUuid(),SocialProvider.KAKAO,socialAccessToken);

        // when
        ResponseEntity<DataResponse<Void>> response = socialIdentityLinkRestController.linkSocialAccount(userDetails,createTestKakaoLoginRequest(),SocialProvider.KAKAO);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().toString()).isEqualTo(DataResponse.ok().toString());
        verify(socialIdentityLinkController).linkSocialAccount(userDetails.getUuid(), SocialProvider.KAKAO, socialAccessToken);
    }

    @Test
    @DisplayName("소셜 연동 해제 성공 시 성공 응답을 반환한다.")
    void testUnlinkSocialAccount_givenCode_willReturnSuccessResponse() {
        // given
        DefaultUserDetails userDetails = testDefaultMemberUserDetailsBuilder.build();
        String socialAccessToken = "social-access-token";
        given(socialIdentityController.issueSocialAccessToken(SocialProvider.KAKAO,TEST_SOCIAL_KAKAO_CODE)).willReturn(socialAccessToken);
        willDoNothing().given(socialIdentityLinkController).unlinkSocialAccount(userDetails.getUuid(),SocialProvider.KAKAO,socialAccessToken);

        // when
        ResponseEntity<DataResponse<Void>> response = socialIdentityLinkRestController.unlinkSocialAccount(userDetails,createTestKakaoLoginRequest(),SocialProvider.KAKAO);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().toString()).isEqualTo(DataResponse.ok().toString());
        verify(socialIdentityLinkController).unlinkSocialAccount(userDetails.getUuid(), SocialProvider.KAKAO, socialAccessToken);
    }
}