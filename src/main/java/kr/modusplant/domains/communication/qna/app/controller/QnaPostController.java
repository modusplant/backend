package kr.modusplant.domains.communication.qna.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import kr.modusplant.domains.communication.common.app.http.response.PostPageResponse;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationPageNumber;
import kr.modusplant.domains.communication.common.domain.validation.CommunicationTitle;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostUpdateRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.communication.qna.app.service.QnaPostApplicationService;
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
import static kr.modusplant.domains.member.vo.MemberUuid.SNAKE_MEMB_UUID;
import static kr.modusplant.global.vo.DatabaseFieldName.CATE_UUID;
import static kr.modusplant.global.vo.DatabaseFieldName.ORDER_INFO;

@Tag(name = "Q&A 게시글 API", description = "Q&A 게시글을 다루는 API입니다.")
@RestController
@RequestMapping("/api/v1/qna/posts")
@RequiredArgsConstructor
@Validated
public class QnaPostController {

    private final QnaPostApplicationService qnaPostApplicationService;

    // 임시로 Spring Security 적용 전 인증 우회를 위해 사용
    // gitignore 처리된 yml 파일에 임의로 값을 추가하여 사용
    // TODO : Spring Security 적용 후 정상 인증 로직으로 대체할 것
    @Value("${fake-auth-uuid}")
    private UUID memberUuid;

    @Operation(
            summary = "전체 Q&A 게시글 목록 조회 API",
            description = "전체 Q&A 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getAllQnaPosts(
            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(qnaPostApplicationService.getAll(PageRequest.of(page, PAGE_SIZE)))));
    }

    @Operation(
            summary = "사이트 회원별 Q&A 게시글 목록 조회 API",
            description = "사이트 회원별 Q&A 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping("/member/{memb_uuid}")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getQnaPostsByMember(
            @PathVariable(required = false, value = SNAKE_MEMB_UUID)
            @NotNull(message = "회원 식별자가 비어 있습니다.")
            UUID memberUuid,

            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(qnaPostApplicationService.getByMemberUuid(memberUuid, PageRequest.of(page, PAGE_SIZE)))));
    }

    @Operation(
            summary = "항목별 Q&A 게시글 목록 조회 API",
            description = "항목별 Q&A 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping("/category/{cate_uuid}")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getQnaPostsByQnaCategory(
            @PathVariable(required = false, value = CATE_UUID)
            @NotNull(message = "항목 식별자가 비어 있습니다.")
            UUID categoryUuid,

            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(qnaPostApplicationService.getByCategoryUuid(categoryUuid, PageRequest.of(page, PAGE_SIZE)))));
    }

    @Operation(
            summary = "제목 + 본문 검색어로 Q&A 게시글 목록 조회 API",
            description = "제목 + 본문 검색어로 Q&A 게시글의 목록과 페이지 정보를 조회합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> searchQnaPosts(
            @RequestParam
            @NotBlank(message = "키워드가 비어 있습니다.")
            String keyword,

            @RequestParam
            @CommunicationPageNumber
            Integer page) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(qnaPostApplicationService.searchByKeyword(keyword, PageRequest.of(page, PAGE_SIZE)))));
    }

    @Operation(
            summary = "특정 Q&A 게시글 조회 API",
            description = "게시글 식별자로 특정 Q&A 게시글을 조회합니다."
    )
    @GetMapping("/{ulid}")
    public ResponseEntity<DataResponse<?>> getQnaPostByUlid(
            @PathVariable
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        Optional<QnaPostResponse> optionalQnaPostResponse = qnaPostApplicationService.getByUlid(ulid);
        if (optionalQnaPostResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalQnaPostResponse.orElseThrow()));
    }

    @Operation(
            summary = "Q&A 게시글 추가 API",
            description = "Q&A 게시글을 작성합니다."
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Void>> insertQnaPost(
            @RequestParam(CATE_UUID)
            UUID categoryUuid,

            @RequestParam
            @CommunicationTitle
            String title,

            @RequestPart
            @NotNull(message = "컨텐츠가 비어 있습니다.")
            List<MultipartFile> content,

            @RequestPart(ORDER_INFO)
            @NotNull(message = "순서 정보가 비어 있습니다.")
            List<@Valid FileOrder> orderInfo
    ) throws IOException {
        qnaPostApplicationService.insert(new QnaPostInsertRequest(categoryUuid, title, content, orderInfo), memberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "특정 Q&A 게시글 수정 API",
            description = "특정 Q&A 게시글을 수정합니다."
    )
    @PutMapping(value = "/{ulid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Void>> updateQnaPost(
            @RequestParam(CATE_UUID)
            @NotNull(message = "항목 식별자가 비어 있습니다.")
            UUID categoryUuid,

            @RequestParam
            @CommunicationTitle
            String title,

            @RequestPart
            @NotNull(message = "컨텐츠가 비어 있습니다.")
            List<MultipartFile> content,

            @RequestPart(ORDER_INFO)
            @NotNull(message = "순서 정보가 비어 있습니다.")
            List<@Valid FileOrder> orderInfo,

            @PathVariable
            String ulid
    ) throws IOException {
        qnaPostApplicationService.update(new QnaPostUpdateRequest(ulid, categoryUuid, title, content, orderInfo), memberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "특정 Q&A 게시글 삭제 API",
            description = "특정 Q&A 게시글을 삭제합니다."
    )
    @DeleteMapping("/{ulid}")
    public ResponseEntity<DataResponse<Void>> removeQnaPostByUlid(
            @PathVariable
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) throws IOException {
        qnaPostApplicationService.removeByUlid(ulid,memberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(
            summary = "특정 Q&A 게시글 조회수 조회 API",
            description = "특정 Q&A 게시글의 조회수를 조회합니다."
    )
    @GetMapping("/{ulid}/views")
    public ResponseEntity<DataResponse<Long>> countViewCount(
            @PathVariable
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(qnaPostApplicationService.readViewCount(ulid)));
    }

    @Operation(
            summary = "특정 Q&A 게시글 조회수 증가 API",
            description = "특정 Q&A 게시글의 조회수를 증가시킵니다."
    )
    @PatchMapping("/{ulid}/views")
    public ResponseEntity<DataResponse<Long>> increaseViewCount(
            @PathVariable
            @NotBlank(message = "게시글 식별자가 비어 있습니다.")
            String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(qnaPostApplicationService.increaseViewCount(ulid, memberUuid)));
    }
}
