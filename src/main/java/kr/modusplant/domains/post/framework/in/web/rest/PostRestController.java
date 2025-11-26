package kr.modusplant.domains.post.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import kr.modusplant.domains.post.adapter.controller.PostController;
import kr.modusplant.domains.post.domain.exception.EmptyCategoryIdException;
import kr.modusplant.domains.post.usecase.request.FileOrder;
import kr.modusplant.domains.post.usecase.request.PostCategoryRequest;
import kr.modusplant.domains.post.usecase.request.PostInsertRequest;
import kr.modusplant.domains.post.usecase.request.PostUpdateRequest;
import kr.modusplant.domains.post.usecase.response.CursorPageResponse;
import kr.modusplant.domains.post.usecase.response.PostDetailResponse;
import kr.modusplant.framework.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.constant.Regex.REGEX_ULID;

@Tag(name = "컨텐츠 게시글 API", description = "컨텐츠 게시글을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/communication/posts")
@RequiredArgsConstructor
@Validated
public class PostRestController {

    private final PostController postController;

    // TODO : Spring Security 적용 후 정상 인증 로직으로 대체할 것 (현재는 gitignore 처리된 yml 파일에 임의로 값을 추가하여 사용)
    @Value("${TEMP_USER_UUID}")
    private UUID currentMemberUuid;

    @Operation(
            summary = "전체 게시글 목록 조회 API (무한스크롤)",
            description = "전체 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<DataResponse<CursorPageResponse<?>>> getAllPosts(
            @Parameter(schema = @Schema(description = "마지막 게시글 ID (첫 요청 시 생략)", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @RequestParam(name = "lastPostId", required = false)
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다.")
            String lastUlid,

            @Parameter(schema = @Schema(description = "페이지 크기", example = "10",minimum = "1",maximum = "50"))
            @RequestParam
            @Range(min = 1, max = 50)
            Integer size,

            @Parameter(schema = @Schema(description = "1차 항목 식별자", example = "2d9f462d-b50f-4394-928e-5c864f60b09a"))
            @RequestParam(name = "primaryCategoryId", required = false)
            UUID primaryCategoryUuid,

            @Parameter(schema = @Schema(description = "2차 항목 식별자 (복수 선택 가능)", example = "4803f4e8-c982-4631-ba82-234d4fa6e824"))
            @RequestParam(name = "secondaryCategoryId", required = false)
            List<UUID> secondaryCategoryUuids
    ) {
        if(primaryCategoryUuid == null && secondaryCategoryUuids != null && !secondaryCategoryUuids.isEmpty()) {
            throw new EmptyCategoryIdException();
        }
        return ResponseEntity.ok().body(DataResponse.ok(postController.getAll(new PostCategoryRequest(primaryCategoryUuid, secondaryCategoryUuids), currentMemberUuid, lastUlid,size)));
    }

    @Operation(
            summary = "검색어를 통한 게시글 목록 조회 API (무한스크롤)",
            description = "키워드별 컨텐츠 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<DataResponse<CursorPageResponse<?>>> getPostsByKeyword(
            @Parameter(schema = @Schema(description = "마지막 게시글 ID (첫 요청 시 생략)", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @RequestParam(name = "lastPostId", required = false)
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다.")
            String lastUlid,

            @Parameter(schema = @Schema(description = "페이지 크기", example = "10",minimum = "1",maximum = "50"))
            @RequestParam
            @Range(min = 1, max = 50)
            Integer size,

            @Parameter(schema = @Schema(description = "{제목+본문} 검색어 키워드", example = "벌레"))
            @RequestParam(required = false)
            String keyword
    ) {
        return ResponseEntity.ok().body(DataResponse.ok(postController.getByKeyword(keyword, currentMemberUuid, lastUlid, size)));
    }

    @Operation(
            summary = "특정 컨텐츠 게시글 조회 API",
            description = "게시글 식별자로 특정 컨텐츠 게시글을 조회합니다."
    )
    @GetMapping("/{postId}")
    public ResponseEntity<DataResponse<?>> getPostByUlid(
            @Parameter(schema = @Schema(description = "게시글의 식별자", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @PathVariable(name = "postId")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다.")
            String ulid
    ) {
        Optional<PostDetailResponse> optionalPostResponse = postController.getByUlid(ulid,currentMemberUuid);
        if (optionalPostResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalPostResponse.orElseThrow()));
    }

    @Operation(
            summary = "컨텐츠 게시글 추가 API",
            description = "컨텐츠 게시글을 작성합니다."
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Void>> insertPost(
            @Parameter(schema = @Schema(description = "게시글이 포함된 1차 항목의 식별자", example = "148d6e33-102d-4df4-a4d0-5ff233665548"))
            @RequestParam(name = "primaryCategoryId")
            @NotNull(message = "1차 항목 식별자가 비어 있습니다.")
            UUID primaryCategoryUuid,

            @Parameter(schema = @Schema(description = "게시글이 포함된 2차 항목의 식별자", example = "148d6e33-102d-4df4-a4d0-5ff233665548"))
            @RequestParam(name = "secondaryCategoryId")
            @NotNull(message = "2차 항목 식별자가 비어 있습니다.")
            UUID secondaryCategoryUuid,

            @Parameter(schema = @Schema(description = "게시글의 제목", maximum = "60", example = "이거 과습인가요?"))
            @RequestParam
            @NotBlank(message = "게시글 제목이 비어 있습니다.")
            @Length(max = 60, message = "게시글 제목은 최대 60글자까지 작성할 수 있습니다.")
            String title,

            @Parameter(schema = @Schema(description = "게시글 컨텐츠"))
            @RequestPart
            @NotEmpty(message = "게시글이 비어 있습니다.")
            List<MultipartFile> content,

            @Parameter(schema = @Schema(description = "게시글에 속한 파트들의 순서에 대한 정보"))
            @RequestPart
            @NotEmpty(message = "순서 정보가 비어 있습니다.")
            List<@Valid FileOrder> orderInfo,

            @Parameter(schema = @Schema(description = "게시글 발행 유무"))
            @RequestParam
            @NotNull(message = "게시글 발행 유무가 비어 있습니다.")
            Boolean isPublished
    ) throws IOException {
        postController.createPost(new PostInsertRequest(primaryCategoryUuid, secondaryCategoryUuid, title, content, orderInfo, isPublished), currentMemberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "특정 컨텐츠 게시글 수정 API",
            description = "특정 컨텐츠 게시글을 수정합니다."
    )
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Void>> updatePost(
            @Parameter(schema = @Schema(description = "게시글 식별을 위한 게시글 식별자", example = "01JXEDF9SNSMAVBY8Z3P5YXK5J"))
            @PathVariable(name = "postId")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다.")
            String ulid,

            @Parameter(schema = @Schema(description = "게시글이 포함된 1차 항목의 식별자", example = "148d6e33-102d-4df4-a4d0-5ff233665548"))
            @RequestParam(name = "primaryCategoryId")
            @NotNull(message = "1차 항목 식별자가 비어 있습니다.")
            UUID primaryCategoryUuid,

            @Parameter(schema = @Schema(description = "게시글이 포함된 2차 항목의 식별자", example = "148d6e33-102d-4df4-a4d0-5ff233665548"))
            @RequestParam(name = "secondaryCategoryId")
            @NotNull(message = "2차 항목 식별자가 비어 있습니다.")
            UUID secondaryCategoryUuid,

            @Parameter(schema = @Schema(description = "게시글의 제목", maximum = "60", example = "이거 과습인가요?"))
            @RequestParam
            @NotBlank(message = "게시글 제목이 비어 있습니다.")
            @Length(max = 60, message = "게시글 제목은 최대 60글자까지 작성할 수 있습니다.")
            String title,

            @Parameter(schema = @Schema(description = "게시글 컨텐츠"))
            @RequestPart
            @NotEmpty(message = "게시글이 비어 있습니다.")
            List<MultipartFile> content,

            @Parameter(schema = @Schema(description = "게시글에 속한 파트들의 순서에 대한 정보"))
            @RequestPart
            @NotEmpty(message = "순서 정보가 비어 있습니다.")
            List<@Valid FileOrder> orderInfo,

            @Parameter(schema = @Schema(description = "게시글 발행 유무"))
            @RequestParam
            @NotNull(message = "게시글 발행 유무가 비어 있습니다.")
            Boolean isPublished
    ) throws IOException {
        postController.updatePost(new PostUpdateRequest(ulid, primaryCategoryUuid, secondaryCategoryUuid, title, content, orderInfo, isPublished), currentMemberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "특정 컨텐츠 게시글 삭제 API",
            description = "특정 컨텐츠 게시글을 삭제합니다."
    )
    @DeleteMapping("/{postId}")
    public ResponseEntity<DataResponse<Void>> removePostByUlid(
            @Parameter(schema = @Schema(description = "게시글의 식별자", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @PathVariable(name = "postId")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다.")
            String ulid
    ) {
        postController.deletePost(ulid, currentMemberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "특정 컨텐츠 게시글 조회수 조회 API",
            description = "특정 컨텐츠 게시글의 조회수를 조회합니다."
    )
    @GetMapping("/{postId}/views")
    public ResponseEntity<DataResponse<Long>> countViewCount(
            @Parameter(schema = @Schema(description = "게시글의 식별자", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @PathVariable(name = "postId")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다.")
            String ulid
    ) {
        return ResponseEntity.ok().body(DataResponse.ok(postController.readViewCount(ulid)));
    }

    @Operation(
            summary = "특정 컨텐츠 게시글 조회수 증가 API",
            description = "특정 컨텐츠 게시글의 조회수를 증가시킵니다."
    )
    @PatchMapping("/{postId}/views")
    public ResponseEntity<DataResponse<Long>> increaseViewCount(
            @Parameter(schema = @Schema(description = "게시글의 식별자", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @PathVariable(name = "postId")
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            @Pattern(regexp = REGEX_ULID, message = "유효하지 않은 ULID 형식입니다.")
            String ulid
    ) {
        return ResponseEntity.ok().body(DataResponse.ok(postController.increaseViewCount(ulid, currentMemberUuid)));
    }
}
