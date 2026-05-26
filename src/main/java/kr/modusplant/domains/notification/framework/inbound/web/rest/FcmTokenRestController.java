package kr.modusplant.domains.notification.framework.inbound.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.modusplant.domains.notification.adapter.controller.FcmTokenController;
import kr.modusplant.domains.notification.usecase.request.FcmTokenRequest;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "알림 FCM API", description = "알림 FCM을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/notifications/fcm")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "Authorization")
public class FcmTokenRestController {

    private final FcmTokenController fcmTokenController;

    @Operation(
            summary = "FCM 토큰 등록 API",
            description = "FCM 토큰을 등록합니다."
    )
    @PostMapping("/token")
    public ResponseEntity<DataResponse<Void>> registerFcmToken(
            @AuthenticationPrincipal DefaultUserDetails userDetails,

            @RequestBody @Valid FcmTokenRequest request
    ) {
        UUID currentMemberUuid = userDetails.getUuid();
        fcmTokenController.register(request,currentMemberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
