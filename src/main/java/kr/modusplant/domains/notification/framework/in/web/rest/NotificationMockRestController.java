package kr.modusplant.domains.notification.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.domains.notification.adapter.controller.NotificationMockController;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.enums.NotificationActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// TODO: 알림 생성 로직 완성 후 삭제
@Tag(name = "알림함 API", description = "알림함을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/notifications/mock")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
@Profile({"local", "dev"})
public class NotificationMockRestController {

    private final NotificationMockController notificationMockController;

    @Operation(
            summary = "!!! 알림함 로직 테스트를 위한 알림 추가 API (테스트용) !!!",
            description = "실제 기능이 아닌 테스트를 위해 알림을 추가합니다. 테스트가 용이하도록 알림 행위자와 수신자가 현재 로그인한 사용자 기준으로 저장됩니다."
    )
    @PostMapping
    public ResponseEntity<DataResponse<Void>> insertNotification(
            @AuthenticationPrincipal DefaultUserDetails userDetails,

            @Parameter(schema = @Schema(description = "알림 발생 액션", example = "POST_LIKED"))
            @RequestParam
            NotificationActionType action,

            @Parameter(schema = @Schema(description = "알림 발생 액션의 게시글 ID", example = ""))
            @RequestParam(name = "postId")
            @NotBlank
            String postUlid,

            @Parameter(schema = @Schema(description = "알림 발생 액션의 댓글 경로", example = "1.1.1"))
            @RequestParam(required = false)
            String commentPath,

            @Parameter(schema = @Schema(description = "알림 발생 컨텐츠 미리보기 내용", example = "제목입니다"))
            @RequestParam
            String contentPreview
    ) {
        UUID currentMemberUuid = userDetails.getUuid();
        notificationMockController.createMockNotification(currentMemberUuid,action,postUlid,commentPath,contentPreview);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "!!! 알림함 로직 테스트 후 로그인한 사용자의 알림 전체 삭제 API (테스트용) !!!",
            description = "실제 기능이 아닌 알림함 로직 테스트 후 데이터 리셋을 위해 로그인한 사용자의 전체 알림을 삭제합니다."
    )
    @DeleteMapping
    public ResponseEntity<DataResponse<Void>> deleteAllNotifications(
            @AuthenticationPrincipal DefaultUserDetails userDetails
    ) {
        UUID currentMemberUuid = userDetails.getUuid();
        notificationMockController.removeMockNotification(currentMemberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }
}
