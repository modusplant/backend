package kr.modusplant.domains.communication.tip.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import kr.modusplant.domains.communication.common.app.http.response.PostPageResponse;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostUpdateRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.communication.tip.app.service.TipPostApplicationService;
import kr.modusplant.global.app.http.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.member.vo.MemberUuid.SNAKE_MEMB_UUID;
import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Tag(name = "Tipersation Post API")
@RestController
@RequestMapping("/api/v1/tip/posts")
@RequiredArgsConstructor
public class TipPostController {

    private final TipPostApplicationService tipPostApplicationService;

    // 임시로 Spring Security 적용 전 인증 우회를 위해 사용
    // gitignore 처리된 yml 파일에 임의로 값을 추가하여 사용
    // TODO : Spring Security 적용 후 정상 인증 로직으로 대체할 것
    @Value("${fake-auth-uuid}")
    private UUID memberUuid;

    @Operation(summary = "전체 팁 게시글 목록 조회 API", description = "전체 팁 게시글의 목록과 페이지 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getAllTipPosts(Pageable pageable) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(tipPostApplicationService.getAll(pageable))));
    }

    @Operation(summary = "사이트 회원별 팁 게시글 목록 조회 API", description = "사이트 회원별 팁 게시글의 목록과 페이지 정보를 조회합니다.")
    @GetMapping("/member/{memb_uuid}")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getTipPostsByMember(@PathVariable(SNAKE_MEMB_UUID) UUID memberUuid, Pageable pageable) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(tipPostApplicationService.getByMemberUuid(memberUuid, pageable))));
    }

    @Operation(summary = "항목별 팁 게시글 목록 조회 API", description = "항목별 팁 게시글의 목록과 페이지 정보를 조회합니다.")
    @GetMapping("/category/{cate_uuid}")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> getTipPostsByTipCategory(@PathVariable(SNAKE_CATE_UUID) UUID categoryUuid, Pageable pageable) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(tipPostApplicationService.getByCategoryUuid(categoryUuid, pageable))));
    }

    @Operation(summary = "제목+본문 검색어로 팁 게시글 목록 조회 API", description = "제목+본문 검색어로 팁 게시글의 목록과 페이지 정보를 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<DataResponse<PostPageResponse<?>>> searchTipPosts(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok().body(DataResponse.ok(PostPageResponse.from(tipPostApplicationService.searchByKeyword(keyword, pageable))));
    }

    @Operation(summary = "특정 팁 게시글 조회 API", description = "게시글 id로 특정 팁 게시글을 조회합니다.")
    @GetMapping("/{ulid}")
    public ResponseEntity<DataResponse<?>> getTipPostByUlid(@PathVariable String ulid) {
        Optional<TipPostResponse> optionalTipPostResponse = tipPostApplicationService.getByUlid(ulid);
        if (optionalTipPostResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalTipPostResponse.orElseThrow()));
    }

    @Operation(summary = "팁 게시글 추가 API", description = "팁 게시글을 작성합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Void>> insertTipPost(
            @RequestPart(SNAKE_CATE_UUID) UUID categoryUuid,
            @RequestPart String title,
            @RequestPart List<MultipartFile> content,
            @RequestPart(SNAKE_ORDER_INFO) List<FileOrder> orderInfo
    ) throws IOException {
        tipPostApplicationService.insert(new TipPostInsertRequest(categoryUuid, title, content, orderInfo), memberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "특정 팁 게시글 수정 API", description = "특정 팁 게시글을 수정합니다.")
    @PutMapping(value = "/{ulid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Void>> updateTipPost(
            @RequestPart(SNAKE_CATE_UUID) UUID categoryUuid,
            @RequestPart String title,
            @RequestPart List<MultipartFile> content,
            @RequestPart(SNAKE_ORDER_INFO) List<FileOrder> orderInfo,
            @PathVariable String ulid
    ) throws IOException {
        tipPostApplicationService.update(new TipPostUpdateRequest(ulid, categoryUuid, title, content, orderInfo), memberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "특정 팁 게시글 삭제 API", description = "특정 팁 게시글을 삭제합니다.")
    @DeleteMapping("/{ulid}")
    public ResponseEntity<DataResponse<Void>> removeTipPostByUlid(@PathVariable String ulid) throws IOException {
        tipPostApplicationService.removeByUlid(ulid,memberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "특정 팁 게시글 조회수 조회 API", description = "특정 팁 게시글의 조회수를 조회합니다.")
    @GetMapping("/{ulid}/views")
    public ResponseEntity<DataResponse<Long>> countViewCount(@PathVariable String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(tipPostApplicationService.readViewCount(ulid)));
    }

    @Operation(summary = "특정 팁 게시글 조회수 증가 API", description = "특정 팁 게시글의 조회수를 증가시킵니다.")
    @PatchMapping("/{ulid}/views")
    public ResponseEntity<DataResponse<Long>> increaseViewCount(@PathVariable String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(tipPostApplicationService.increaseViewCount(ulid, memberUuid)));
    }
}
