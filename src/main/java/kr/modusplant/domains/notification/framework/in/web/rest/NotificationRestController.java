package kr.modusplant.domains.notification.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import kr.modusplant.domains.notification.adapter.controller.NotificationController;
import kr.modusplant.domains.notification.usecase.response.CursorPageResponse;
import kr.modusplant.domains.notification.usecase.response.NotificationResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.enums.NotificationStatusType;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static kr.modusplant.shared.constant.Regex.REGEX_ULID;

@Tag(name = "알림함 API", description = "알림함을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "Authorization")
public class NotificationRestController {
    private final NotificationController notificationController;

    @Operation(
            summary = "알림 목록 조회 API (무한스크롤)",
            description = "알림 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<DataResponse<CursorPageResponse<NotificationResponse>>> getAllNotifications(
            @AuthenticationPrincipal DefaultUserDetails userDetails,

            @Parameter(schema = @Schema(description = "알림 상태 (전체 목록 조회 시 생략)", example = "unread"))
            @RequestParam(required = false)
            NotificationStatusType status,

            @Parameter(schema = @Schema(description = "마지막 알림 ID (첫 요청 시 생략)", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @RequestParam(name = "lastNotificationId", required = false)
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다.")
            String lastUlid,

            @Parameter(schema = @Schema(description = "페이지 크기", example = "10",minimum = "1",maximum = "50"))
            @RequestParam
            @Range(min = 1, max = 50)
            Integer size
    ) {
        UUID currentMemberUuid = userDetails.getUuid();
        return ResponseEntity.ok().body(DataResponse.ok(notificationController.getNotifications(status, currentMemberUuid, lastUlid, size)));
    }

    @Operation(
            summary = "알림 단건 읽음 처리 API",
            description = "특정 알림을 읽음 처리합니다."
    )
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<DataResponse<Void>> readSingleNotification(
            @AuthenticationPrincipal DefaultUserDetails userDetails,

            @Parameter(schema = @Schema(description = "알림 식별을 위한 알림 식별자", example = "01JXEDF9SNSMAVBY8Z3P5YXK5J"))
            @PathVariable(name = "notificationId")
            @NotBlank(message = "알림 식별자가 비어 있습니다.")
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다.")
            String ulid
    ) {
        UUID currentMemberUuid = userDetails.getUuid();
        notificationController.readNotification(ulid,currentMemberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "알림 모두 읽음 처리 API",
            description = "모든 알림을 읽음 처리합니다."
    )
    @PatchMapping("/read-all")
    public ResponseEntity<DataResponse<Void>> readAllNotifications(
            @AuthenticationPrincipal DefaultUserDetails userDetails
    ) {
        UUID currentMemberUuid = userDetails.getUuid();
        notificationController.readAllNotifications(currentMemberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "읽지 않은 알림 개수 조회 API",
            description = "읽지 않은 알림의 개수를 조회합니다."
    )
    @GetMapping("/unread-count")
    public ResponseEntity<DataResponse<Long>> countUnreadNotifications(
            @AuthenticationPrincipal DefaultUserDetails userDetails
    ) {
        UUID currentMemberUuid = userDetails.getUuid();
        return ResponseEntity.ok().body(DataResponse.ok(notificationController.countUnreadNotifications(currentMemberUuid)));
    }
}
