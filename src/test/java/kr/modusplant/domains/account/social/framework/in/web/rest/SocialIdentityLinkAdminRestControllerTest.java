package kr.modusplant.domains.account.social.framework.in.web.rest;

import kr.modusplant.domains.account.social.adapter.controller.SocialIdentityLinkAdminController;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_ADMIN_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SocialIdentityLinkAdminRestControllerTest {
    private final SocialIdentityLinkAdminController adminController = Mockito.mock(SocialIdentityLinkAdminController.class);
    private final SocialIdentityLinkAdminRestController adminRestController = new SocialIdentityLinkAdminRestController(adminController);
    
    @Test
    @DisplayName("소셜 연동 데이터 제거 성공 시 성공 응답을 반환한다.")
    void testRemoveSocialLinkData_givenMemberId_willReturnSuccessResponse() {
        // given
        willDoNothing().given(adminController).removeSocialLink(MEMBER_BASIC_ADMIN_UUID);

        // when
        ResponseEntity<DataResponse<Void>> response = adminRestController.removeSocialLinkData(MEMBER_BASIC_ADMIN_UUID);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(String.valueOf(response.getBody())).isEqualTo(String.valueOf(DataResponse.ok().toString()));
        verify(adminController, times(1)).removeSocialLink(MEMBER_BASIC_ADMIN_UUID);
    }
}