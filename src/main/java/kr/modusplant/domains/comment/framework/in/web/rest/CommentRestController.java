package kr.modusplant.domains.comment.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.modusplant.domains.comment.adapter.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.adapter.controller.CommentController;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import kr.modusplant.legacy.domains.communication.domain.validation.CommunicationPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "컨텐츠 댓글 API", description = "컨텐츠 댓글 도메인을 다루는 API입니다.")
@RestController
@Primary
@RequestMapping("/api/v1/communication/comments")
@RequiredArgsConstructor
@Validated
public class CommentRestController {

    private final CommentController controller;

    @Operation(
            summary = "컨텐츠 댓글 삽입 API",
            description = "게시글 식별자와 경로, 회원 식별자, 컨텐츠 정보로 컨텐츠 항목을 삽입합니다."
    )
    @PostMapping
    public ResponseEntity<DataResponse<Void>> register(
            @RequestBody @Valid
            CommentRegisterRequest registerRequest) {
        controller.register(registerRequest);

        return ResponseEntity.ok().body(DataResponse.ok());
    }

//    @Operation(
//            summary = "식별자로 컨텐츠 댓글 제거 API",
//            description = "식별자로 컨텐츠 댓글을 제거합니다."
//    )
//    @DeleteMapping("/post/{ulid}/path/{path}")
//    public ResponseEntity<DataResponse<?>> delete(
//            @Parameter(schema = @Schema(
//                    description = "해당 댓글이 달린 게시글의 식별자",
//                    example = "01JY3PPG5YJ41H7BPD0DSQW2RD")
//            )
//            @PathVariable(required = false, value = "ulid")
//            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
//            String postUlid,
//
//            @Parameter(schema = @Schema(
//                    description = "댓글의 구체화된 경로",
//                    pattern = "^\\d+(?:\\.\\d+)*$",
//                    example = "4.8.12")
//            )
//            @PathVariable(required = false, value = "path")
//            @CommunicationPath
//            String path) {
//        controller.removeByPostUlidAndPath(postUlid, path);
//        return ResponseEntity.ok().body(DataResponse.ok());
//    }
}
