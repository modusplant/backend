package kr.modusplant.domains.notification.adapter.controller;

import kr.modusplant.domains.notification.usecase.port.repository.FcmTokenRepository;
import kr.modusplant.domains.notification.usecase.request.FcmTokenRequest;
import kr.modusplant.shared.enums.Platform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


class FcmTokenControllerTest {
    private final FcmTokenRepository fcmTokenRepository = Mockito.mock(FcmTokenRepository.class);
    private final FcmTokenController fcmTokenController = new FcmTokenController(fcmTokenRepository);

    @Test
    @DisplayName("FCM 토큰 등록 요청 시 레포지토리에 저장을 위임한다")
    void register_givenValidRequest_willCallRepository() {
        // given
        String token = "fcm-test-token-12345";
        Platform platform = Platform.WEB;
        FcmTokenRequest request = new FcmTokenRequest(token, platform);
        UUID currentMemberUuid = MEMBER_BASIC_USER_UUID;

        // when
        fcmTokenController.register(request, currentMemberUuid);

        // then
        verify(fcmTokenRepository, times(1)).saveOrUpdate(token, currentMemberUuid, platform);
    }

    @Test
    @DisplayName("토큰값이 null인 요청이 오더라도 컨트롤러는 받은 값을 그대로 레포지토리에 전달한다")
    void register_givenNullToken_willStillCallRepository() {
        // given
        Platform platform = Platform.WEB;
        FcmTokenRequest request = new FcmTokenRequest(null, platform);
        UUID currentMemberUuid = MEMBER_BASIC_USER_UUID;

        // when
        fcmTokenController.register(request, currentMemberUuid);

        // then
        verify(fcmTokenRepository, times(1)).saveOrUpdate(null, currentMemberUuid, platform);
    }
}