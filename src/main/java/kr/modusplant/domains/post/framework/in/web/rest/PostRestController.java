package kr.modusplant.domains.post.framework.in.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.post.adapter.controller.PostController;
import kr.modusplant.domains.post.usecase.request.FileOrder;
import kr.modusplant.domains.post.usecase.request.PostInsertRequest;
import kr.modusplant.domains.post.usecase.request.PostUpdateRequest;
import kr.modusplant.domains.post.usecase.response.PostPageResponse;
import kr.modusplant.domains.post.usecase.response.PostResponse;
import kr.modusplant.domains.post.framework.in.web.validation.CommunicationPageNumber;
import kr.modusplant.framework.out.jackson.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "컨텐츠 게시글 API", description = "컨텐츠 게시글을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/communication/posts")
@RequiredArgsConstructor
@Validated
public class PostRestController {

    private final PostController postController;

    // TODO : Spring Security 적용 후 정상 인증 로직으로 대체할 것 (현재는 gitignore 처리된 yml 파일에 임의로 값을 추가하여 사용)
    @Value("${fake-auth-uuid}")
    private UUID currentMemberUuid;
    // TODO: PAGE_SIZE 별도의 파일로 추출하기
    private static final int PAGE_SIZE = 10;

    @Operation(
            summary = "전체 컨텐츠 게시글 목록 조회 API",
            description = "전체 컨텐츠 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getAllPosts(
            @Parameter(schema = @Schema(description = "페이지 숫자", minimum = "1", example = "4"))
            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(postController.getAll(PageRequest.of(page, PAGE_SIZE)))));
    }

    @Operation(
            summary = "사이트 회원별 컨텐츠 게시글 목록 조회 API",
            description = "사이트 회원별 컨텐츠 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping("/member/{memberUuid}")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getPostsByMember(
            @Parameter(schema = @Schema(description = "회원의 식별자", example = "038ae842-3c93-484f-b526-7c4645a195a7"))
            @PathVariable(required = false)
            @NotNull(message = "회원 식별자가 비어 있습니다.")
            UUID memberUuid,
            @Parameter(schema = @Schema(description = "페이지 숫자", minimum = "1", example = "4"))
            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(postController.getByMemberUuid(memberUuid, PageRequest.of(page, PAGE_SIZE)))));
    }

    @GetMapping("/me/drafts")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getDraftPostsByMember(
            @Parameter(schema = @Schema(description = "페이지 숫자", minimum = "1", example = "4"))
            @RequestParam
            @CommunicationPageNumber
            Integer page
    ) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(postController.getDraftByMemberUuid(currentMemberUuid,PageRequest.of(page,PAGE_SIZE)))));
    }

    @Operation(
            summary = "1차 항목별 컨텐츠 게시글 목록 조회 API",
            description = "1차 항목별 컨텐츠 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping("/category/primary/{primaryCategoryUuid}")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getPostsByPrimaryCategory(
            @Parameter(schema = @Schema(description = "1차 항목 식별자", example = "2d9f462d-b50f-4394-928e-5c864f60b09a"))
            @PathVariable(required = false)
            @NotNull(message = "1차 항목 식별자가 비어 있습니다.")
            UUID primaryCategoryUuid,

            @Parameter(schema = @Schema(
                    description = "페이지 숫자",
                    minimum = "1",
                    example = "4")
            )
            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(postController.getByPrimaryCategoryUuid(primaryCategoryUuid, PageRequest.of(page, PAGE_SIZE)))));
    }

    @Operation(
            summary = "2차 항목별 컨텐츠 게시글 목록 조회 API",
            description = "2차 항목별 컨텐츠 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping("/category/secondary/{secondaryCategoryUuid}")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getPostsBySecondaryCategory(
            @Parameter(schema = @Schema(description = "2차 항목 식별자", example = "4803f4e8-c982-4631-ba82-234d4fa6e824"))
            @PathVariable(required = false)
            @NotNull(message = "2차 항목 식별자가 비어 있습니다.")
            UUID secondaryCategoryUuid,

            @Parameter(schema = @Schema(description = "페이지 숫자", minimum = "1", example = "4"))
            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(postController.getBySecondaryCategoryUuid(secondaryCategoryUuid, PageRequest.of(page, PAGE_SIZE)))));
    }

    @Operation(
            summary = "제목 + 본문 검색어로 컨텐츠 게시글 목록 조회 API",
            description = "제목 + 본문 검색어로 컨텐츠 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> searchPosts(
            @Parameter(schema = @Schema(description = "검색 키워드", example = "벌레"))
            @RequestParam
            @NotBlank(message = "키워드가 비어 있습니다.")
            String keyword,

            @Parameter(schema = @Schema(description = "페이지 숫자", minimum = "1", example = "4"))
            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(postController.searchByKeyword(keyword, PageRequest.of(page, PAGE_SIZE)))));
    }

    @Operation(
            summary = "특정 컨텐츠 게시글 조회 API",
            description = "게시글 식별자로 특정 컨텐츠 게시글을 조회합니다."
    )
    @GetMapping("/{ulid}")
    public ResponseEntity<DataResponse<?>> getPostByUlid(
            @Parameter(schema = @Schema(description = "게시글의 식별자", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        Optional<PostResponse> optionalPostResponse = postController.getByUlid(ulid,currentMemberUuid);
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
            @RequestParam
            @NotNull(message = "1차 항목 식별자가 비어 있습니다.")
            UUID primaryCategoryUuid,

            @Parameter(schema = @Schema(description = "게시글이 포함된 2차 항목의 식별자", example = "148d6e33-102d-4df4-a4d0-5ff233665548"))
            @RequestParam
            @NotNull(message = "2차 항목 식별자가 비어 있습니다.")
            UUID secondaryCategoryUuid,

            @Parameter(schema = @Schema(description = "게시글의 제목", maximum = "150", example = "이거 과습인가요?"))
            @RequestParam
            @NotBlank(message = "게시글 제목이 비어 있습니다.")
            @Length(max = 150, message = "게시글 제목은 최대 150글자까지 작성할 수 있습니다.")
            String title,

            @Parameter(schema = @Schema(description = "게시글 컨텐츠"))
            @RequestPart
            @NotNull(message = "게시글이 비어 있습니다.")
            List<MultipartFile> content,

            @Parameter(schema = @Schema(description = "게시글에 속한 파트들의 순서에 대한 정보"))
            @RequestPart
            @NotNull(message = "순서 정보가 비어 있습니다.")
            List<@Valid FileOrder> orderInfo,

            @RequestParam
            @Parameter(schema = @Schema(description = "게시글 발행 유무"))
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
    @PutMapping(value = "/{ulid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Void>> updatePost(
            @Parameter(schema = @Schema(description = "갱신을 위한 1차 항목 식별자", example = "e493d48f-0ae6-4572-b624-f8f468515c71"))
            @RequestParam
            @NotNull(message = "1차 항목 식별자가 비어 있습니다.")
            UUID primaryCategoryUuid,

            @Parameter(schema = @Schema(description = "갱신을 위한 2차 항목 식별자", example = "bde79fd5-083d-425c-b71b-69a157fc5739"))
            @RequestParam
            @NotNull(message = "2차 항목 식별자가 비어 있습니다.")
            UUID secondaryCategoryUuid,

            @Parameter(schema = @Schema(description = "갱신을 위한 게시글 제목", example = "이거 과습인지 아시는 분!"))
            @RequestParam
            @NotBlank(message = "게시글 제목이 비어 있습니다.")
            @Length(max = 150, message = "게시글 제목은 최대 150글자까지 작성할 수 있습니다.")
            String title,

            @Parameter(schema = @Schema(description = "갱신을 위한 게시글 컨텐츠"))
            @RequestPart
            @NotNull(message = "컨텐츠가 비어 있습니다.")
            List<MultipartFile> content,

            @Parameter(schema = @Schema(description = "게시글에 속한 파트들의 순서에 대한 정보"))
            @RequestPart
            @NotNull(message = "순서 정보가 비어 있습니다.")
            List<@Valid FileOrder> orderInfo,

            @Parameter(schema = @Schema(description = "게시글 식별을 위한 게시글 식별자", example = "01JXEDF9SNSMAVBY8Z3P5YXK5J"))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid,

            @RequestParam
            @Parameter(schema = @Schema(description = "게시글 발행 유무"))
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
    @DeleteMapping("/{ulid}")
    public ResponseEntity<DataResponse<Void>> removePostByUlid(
            @Parameter(schema = @Schema(description = "게시글의 식별자", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) throws IOException {
        postController.deletePost(ulid, currentMemberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "특정 컨텐츠 게시글 조회수 조회 API",
            description = "특정 컨텐츠 게시글의 조회수를 조회합니다."
    )
    @GetMapping("/{ulid}/views")
    public ResponseEntity<DataResponse<Long>> countViewCount(
            @Parameter(schema = @Schema(description = "게시글의 식별자", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(postController.readViewCount(ulid)));
    }

    @Operation(
            summary = "특정 컨텐츠 게시글 조회수 증가 API",
            description = "특정 컨텐츠 게시글의 조회수를 증가시킵니다."
    )
    @PatchMapping("/{ulid}/views")
    public ResponseEntity<DataResponse<Long>> increaseViewCount(
            @Parameter(schema = @Schema(description = "게시글의 식별자", example = "01JY3PPG5YJ41H7BPD0DSQW2RD"))
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(postController.increaseViewCount(ulid, currentMemberUuid)));
    }
}
