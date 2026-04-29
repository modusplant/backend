package kr.modusplant.domains.notification.adapter.controller;

import kr.modusplant.domains.notification.usecase.port.repository.FcmTokenRepository;
import kr.modusplant.domains.notification.usecase.request.FcmTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void register(FcmTokenRequest fcmTokenRequest, UUID currentMemberUuid) {
        fcmTokenRepository.saveOrUpdate(fcmTokenRequest.token(), currentMemberUuid, fcmTokenRequest.platform());
    }

}
