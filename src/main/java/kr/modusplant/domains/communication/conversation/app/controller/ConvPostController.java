package kr.modusplant.domains.communication.conversation.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import kr.modusplant.domains.communication.common.app.http.response.PostPageResponse;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationPageNumber;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationTitle;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvPostInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvPostUpdateRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvPostResponse;
import kr.modusplant.domains.communication.conversation.app.service.ConvPostApplicationService;
import kr.modusplant.global.app.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
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

import static kr.modusplant.domains.communication.common.vo.CommPageableValue.PAGE_SIZE;

@Tag(name = "대화 게시글 API", description = "대화 게시글을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/conversation/posts")
@RequiredArgsConstructor
@Validated
public class ConvPostController {

    private final ConvPostApplicationService convPostApplicationService;

    // 임시로 Spring Security 적용 전 인증 우회를 위해 사용
    // gitignore 처리된 yml 파일에 임의로 값을 추가하여 사용
    // TODO : Spring Security 적용 후 정상 인증 로직으로 대체할 것
    @Value("${fake-auth-uuid}")
    private UUID memberUuid;

    @Operation(
            summary = "전체 대화 게시글 목록 조회 API",
            description = "전체 대화 게시글과 페이지 정보를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getAllConvPosts(
            @Parameter(schema = @Schema(
                    description = "페이지 숫자",
                    minimum = "1",
                    example = "3")
            )
            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(convPostApplicationService.getAll(PageRequest.of(page, PAGE_SIZE)))));
    }

    @Operation(
            summary = "사이트 회원별 대화 게시글 목록 조회 API",
            description = "사이트 회원별 대화 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping("/member/{memberUuid}")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getConvPostsByMember(
            @Parameter(schema = @Schema(
                    description = "회원의 식별자",
                    example = "fcf1a3d0-45a2-4490-bbef-1f5bff40c5bc")
            )
            @PathVariable(required = false)
            @NotNull(message = "회원 식별자가 비어 있습니다.")
            UUID memberUuid,

            @Parameter(schema = @Schema(
                    description = "페이지 숫자",
                    minimum = "1",
                    example = "3")
            )
            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(convPostApplicationService.getByMemberUuid(memberUuid, PageRequest.of(page, PAGE_SIZE)))));
    }

    @Operation(
            summary = "항목별 대화 게시글 목록 조회 API",
            description = "항목별 대화 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping("/category/{categoryUuid}")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getConvPostsByConvCategory(
            @Parameter(schema = @Schema(
                    description = "대화 항목 식별자",
                    example = "4c3fad03-13ff-4c95-98bc-bdffa95e3299")
            )
            @PathVariable(required = false)
            @NotNull(message = "항목 식별자가 비어 있습니다.")
            UUID categoryUuid,

            @Parameter(schema = @Schema(
                    description = "페이지 숫자",
                    minimum = "1",
                    example = "3")
            )
            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(convPostApplicationService.getByCategoryUuid(categoryUuid, PageRequest.of(page, PAGE_SIZE)))));
    }

    @Operation(
            summary = "제목+본문 검색어로 대화 게시글 목록 조회 API",
            description = "제목+본문 검색어로 대화 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> searchConvPosts(
            @Parameter(schema = @Schema(
                    description = "검색 키워드",
                    example = "베란다")
            )
            @RequestParam
            @NotBlank(message = "키워드가 비어 있습니다.")
            String keyword,

            @Parameter(schema = @Schema(
                    description = "페이지 숫자",
                    minimum = "1",
                    example = "3")
            )
            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(convPostApplicationService.searchByKeyword(keyword, PageRequest.of(page, PAGE_SIZE)))));
    }

    @Operation(
            summary = "특정 대화 게시글 조회 API",
            description = "게시글 식별자로 특정 대화 게시글을 조회합니다."
    )
    @GetMapping("/{ulid}")
    public ResponseEntity<DataResponse<?>> getConvPostByUlid(
            @Parameter(schema = @Schema(
                    description = "게시글의 식별자",
                    example = "01JY3PNDD6EWHV8PS1WDBWCZPH")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        Optional<ConvPostResponse> optionalConvPostResponse = convPostApplicationService.getByUlid(ulid);
        if (optionalConvPostResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalConvPostResponse.orElseThrow()));
    }

    @Operation(
            summary = "대화 게시글 추가 API",
            description = "대화 게시글을 작성합니다."
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Void>> insertConvPost(
            @Parameter(schema = @Schema(
                    description = "게시글이 포함된 항목의 식별자",
                    example = "5f989e30-b0b8-4e59-a733-f7b5c8d901f8")

            )
            @RequestParam
            @NotNull(message = "항목 식별자가 비어 있습니다.")
            UUID categoryUuid,

            @Parameter(schema = @Schema(
                    description = "게시글의 제목",
                    maximum = "150",
                    example = "우리 집 식물 구경하세요~")
            )
            @RequestParam
            @CommunicationTitle
            String title,

            @Parameter(schema = @Schema(
                    description = "게시글 컨텐츠")
            )
            @RequestPart
            @NotNull(message = "게시글이 비어 있습니다.")
            List<MultipartFile> content,

            @Parameter(schema = @Schema(
                    description = "게시글에 속한 파트들의 순서에 대한 정보")
            )
            @RequestPart
            @NotNull(message = "순서 정보가 비어 있습니다.")
            List<@Valid FileOrder> orderInfo
    ) throws IOException {
        convPostApplicationService.insert(new ConvPostInsertRequest(categoryUuid, title, content, orderInfo), memberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "특정 대화 게시글 수정 API",
            description = "특정 대화 게시글을 수정합니다."
    )
    @PutMapping(value = "/{ulid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Void>> updateConvPost(
            @Parameter(schema = @Schema(
                    description = "갱신을 위한 게시글 항목 식별자",
                    example = "12941529-1ecf-4f9c-afe0-6c2be065bf8d")
            )
            @RequestParam
            @NotNull(message = "항목 식별자가 비어 있습니다.")
            UUID categoryUuid,

            @Parameter(schema = @Schema(
                    description = "갱신을 위한 게시글 제목",
                    example = "우리 집 식물을 공개합니다!")
            )
            @RequestParam
            @CommunicationTitle
            String title,

            @Parameter(schema = @Schema(
                    description = "갱신을 위한 게시글 컨텐츠")
            )
            @RequestPart
            @NotNull(message = "컨텐츠가 비어 있습니다.")
            List<MultipartFile> content,

            @Parameter(schema = @Schema(
                    description = "게시글에 속한 파트들의 순서에 대한 정보")
            )
            @RequestPart
            @NotNull(message = "순서 정보가 비어 있습니다.")
            List<@Valid FileOrder> orderInfo,

            @Parameter(schema = @Schema(
                    description = "게시글 식별을 위한 게시글 식별자",
                    example = "01ARZ3NDEKTSV4RRFFQ69G5FAV")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid
    ) throws IOException {
        convPostApplicationService.update(new ConvPostUpdateRequest(ulid, categoryUuid, title, content, orderInfo), memberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "특정 대화 게시글 삭제 API",
            description = "특정 대화 게시글을 삭제합니다."
    )
    @DeleteMapping("/{ulid}")
    public ResponseEntity<DataResponse<Void>> removeConvPostByUlid(
            @Parameter(schema = @Schema(
                    description = "게시글의 식별자",
                    example = "01JY3PNDD6EWHV8PS1WDBWCZPH")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) throws IOException {
        convPostApplicationService.removeByUlid(ulid, memberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "특정 대화 게시글 조회수 조회 API",
            description = "특정 대화 게시글의 조회수를 조회합니다."
    )
    @GetMapping("/{ulid}/views")
    public ResponseEntity<DataResponse<Long>> countViewCount(
            @Parameter(schema = @Schema(
                    description = "게시글의 식별자",
                    example = "01JY3PNDD6EWHV8PS1WDBWCZPH")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(convPostApplicationService.readViewCount(ulid)));
    }

    @Operation(
            summary = "특정 대화 게시글 조회수 증가 API",
            description = "특정 대화 게시글의 조회수를 증가시킵니다."
    )
    @PatchMapping("/{ulid}/views")
    public ResponseEntity<DataResponse<Long>> increaseViewCount(
            @Parameter(schema = @Schema(
                    description = "게시글의 식별자",
                    example = "01JY3PNDD6EWHV8PS1WDBWCZPH")
            )
            @PathVariable(required = false)
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(convPostApplicationService.increaseViewCount(ulid, memberUuid)));
    }
}
