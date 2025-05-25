package kr.modusplant.domains.conversation.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.modusplant.domains.conversation.app.http.request.FileOrder;
import kr.modusplant.domains.conversation.app.http.request.ConvPostRequest;
import kr.modusplant.domains.conversation.app.http.response.ConvPostPageResponse;
import kr.modusplant.domains.conversation.app.http.response.ConvPostResponse;
import kr.modusplant.domains.conversation.app.service.ConvPostApplicationService;
import kr.modusplant.global.app.servlet.response.DataResponse;
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

@Tag(name = "Conversation Post API")
@RestController
@RequestMapping("/api/v1/conv/posts")
@RequiredArgsConstructor
public class ConvPostController {

    private final ConvPostApplicationService convPostApplicationService;

    // 임시로 Spring Security 적용 전 인증 우회를 위해 사용
    // gitignore 처리된 yml 파일에 임의로 값을 추가하여 사용
    // TODO : Spring Security 적용 후 정상 인증 로직으로 대체할 것
    @Value("${fake-auth-uuid}")
    private UUID memberUuid;

    @Operation(summary = "전체 팁 게시글 목록 조회 API", description = "전체 팁 게시글의 목록과 페이지 정보를 조회합니다.")
    @GetMapping("")
    public ResponseEntity<DataResponse<ConvPostPageResponse>> getAllConvPosts(Pageable pageable) {
        return ResponseEntity.ok().body(DataResponse.ok(ConvPostPageResponse.from(convPostApplicationService.getAll(pageable))));
    }

    @Operation(summary = "사이트 회원별 팁 게시글 목록 조회 API", description = "사이트 회원별 팁 게시글의 목록과 페이지 정보를 조회합니다.")
    @GetMapping("/members/{memb_uuid}")
    public ResponseEntity<DataResponse<ConvPostPageResponse>> getConvPostsByMember(@PathVariable("memb_uuid") UUID memberUuid, Pageable pageable) {
        return ResponseEntity.ok().body(DataResponse.ok(ConvPostPageResponse.from(convPostApplicationService.getByMemberUuid(memberUuid,pageable))));
    }

    @Operation(summary = "식물 그룹별 팁 게시글 목록 조회 API", description = "식물 그룹별 팁 게시글의 목록과 페이지 정보를 조회합니다.")
    @GetMapping("/plant-groups/{group_id}")
    public ResponseEntity<DataResponse<ConvPostPageResponse>> getConvPostsByPlantGroup(@PathVariable("group_id") Integer groupOrder, Pageable pageable) {
        return ResponseEntity.ok().body(DataResponse.ok(ConvPostPageResponse.from(convPostApplicationService.getByGroupOrder(groupOrder,pageable))));
    }

    @Operation(summary = "제목+본문 검색어로 팁 게시글 목록 조회 API", description = "제목+본문 검색어로 팁 게시글의 목록과 페이지 정보를 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<DataResponse<ConvPostPageResponse>> searchConvPosts(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok().body(DataResponse.ok(ConvPostPageResponse.from(convPostApplicationService.searchByKeyword(keyword,pageable))));
    }

    @Operation(summary = "특정 팁 게시글 조회 API", description = "게시글 id로 특정 팁 게시글을 조회합니다.")
    @GetMapping("/{ulid}")
    public ResponseEntity<DataResponse<?>> getConvPostByUlid(@PathVariable String ulid) {
        Optional<ConvPostResponse> optionalConvPostResponse = convPostApplicationService.getByUlid(ulid);
        if (optionalConvPostResponse.isEmpty()) {
            return ResponseEntity.ok().body(DataResponse.ok());
        }
        return ResponseEntity.ok().body(DataResponse.ok(optionalConvPostResponse.orElseThrow()));
    }

    @Operation(summary = "팁 게시글 추가 API", description = "팁 게시글을 작성합니다.")
    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Void>> insertConvPost(
            @RequestPart("group_order") Integer groupOrder,
            @RequestPart String title,
            @RequestPart List<MultipartFile> content,
            @RequestPart("order_info") List<FileOrder> orderInfo
    ) throws IOException {
        convPostApplicationService.insert(new ConvPostRequest(groupOrder,title,content,orderInfo),memberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "특정 팁 게시글 수정 API", description = "특정 팁 게시글을 수정합니다.")
    @PutMapping(value = "/{ulid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Void>> updateConvPost(
            @RequestPart(value = "group_order", required = false) Integer groupOrder,
            @RequestPart(required = false) String title,
            @RequestPart(required = false) List<MultipartFile> content,
            @RequestPart(value = "order_info", required = false) List<FileOrder> orderInfo,
            @PathVariable String ulid
    ) throws IOException {
        convPostApplicationService.update(new ConvPostRequest(groupOrder,title,content,orderInfo), ulid, memberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "특정 팁 게시글 삭제 API", description = "특정 팁 게시글을 삭제합니다.")
    @DeleteMapping("/{ulid}")
    public ResponseEntity<DataResponse<Void>> removeConvPostByUlid(@PathVariable String ulid) throws IOException {
        convPostApplicationService.removeByUlid(ulid,memberUuid);
        return ResponseEntity.ok().body(DataResponse.ok());
    }

    @Operation(summary = "특정 팁 게시글 조회수 조회 API", description = "특정 팁 게시글의 조회수를 조회합니다.")
    @GetMapping("/{ulid}/views")
    public ResponseEntity<DataResponse<Long>> countViewCount(@PathVariable String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(convPostApplicationService.countViews(ulid)));
    }

    @Operation(summary = "특정 팁 게시글 조회수 증가 API", description = "특정 팁 게시글의 조회수를 증가시킵니다.")
    @PatchMapping("/{ulid}/views")
    public ResponseEntity<DataResponse<Long>> increaseViewCount(@PathVariable String ulid) {
        return ResponseEntity.ok().body(DataResponse.ok(convPostApplicationService.increase(ulid, memberUuid)));
    }
}
