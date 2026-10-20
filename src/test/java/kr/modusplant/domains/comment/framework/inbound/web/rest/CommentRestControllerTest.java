package kr.modusplant.domains.comment.framework.inbound.web.rest;

import kr.modusplant.domains.comment.adapter.controller.CommentController;
import kr.modusplant.domains.comment.common.util.domain.vo.PostIdTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.request.CommentRegisterRequestTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.request.CommentUpdateRequestTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.response.CommentCursorPageResponseTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.response.CommentOfPostResponseTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.response.CommentOffsetPageResponseTestUtils;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.inbound.web.cache.CommentCacheService;
import kr.modusplant.domains.comment.framework.inbound.web.cache.model.CommentCacheData;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.response.CommentCursorPageResponse;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentOffsetPageResponse;
import kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.infrastructure.security.models.DefaultUserDetails;
import kr.modusplant.shared.framework.jackson.http.response.DataResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PAGE_SIZE;
import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_ADMIN_UUID;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

public class CommentRestControllerTest implements PostIdTestUtils,
        CommentOfPostResponseTestUtils, CommentRegisterRequestTestUtils,
        CommentUpdateRequestTestUtils, MemberIdTestUtils,
        CommentOffsetPageResponseTestUtils, CommentCursorPageResponseTestUtils {
    private final CommentController controller = Mockito.mock(CommentController.class);
    private final CommentCacheService cacheService = Mockito.mock(CommentCacheService.class);
    private final CommentRestController restController = new CommentRestController(controller, cacheService);

    private final String testIfNoneMatch = "\"abc123\"";
    private final String testIfModifiedSince = "Sat, 01 Jan 2025 00:00:00 GMT";

    @Test
    @DisplayName("postUlid 기반 조회 시 캐시 가능한 경우 ResponseEntity 반환")
    void testGatherByPost_V1_givenCacheableResult_willReturnResponseEntity() {
        // given
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), true);
        given(cacheService.getCacheData(testIfNoneMatch, testIfModifiedSince, PostId.create(TEST_POST_ULID), null, null))
                .willReturn(cacheData);

        // when
        ResponseEntity<DataResponse<List<CommentOfPostResponse>>> result =
                restController.gatherByPost_V1(TEST_POST_ULID, testIfNoneMatch, testIfModifiedSince, null);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_MODIFIED);
        assertThat(result.getBody()).isNull();
        then(controller).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("postUlid 기반 조회 시 캐시 불가능한 경우 ResponseEntity 반환")
    void testGatherByPost_V1_givenNotCacheableResultWithAuthenticatedMember_willReturnResponseEntity() {
        // given
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), false);
        given(cacheService.getCacheData(testIfNoneMatch, testIfModifiedSince, PostId.create(TEST_POST_ULID), null, null))
                .willReturn(cacheData);
        given(controller.gatherByPost_V1(TEST_POST_ULID, MEMBER_BASIC_USER_UUID))
                .willReturn(List.of(testCommentOfPostResponse));
        DefaultUserDetails userDetails = DefaultUserDetails.builder().uuid(MEMBER_BASIC_USER_UUID).build();

        // when
        ResponseEntity<DataResponse<List<CommentOfPostResponse>>> result =
                restController.gatherByPost_V1(TEST_POST_ULID, testIfNoneMatch, testIfModifiedSince, userDetails);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(result.getBody());
        assertThat(result.getBody().getData()).isEqualTo(List.of(testCommentOfPostResponse));
    }

    @Test
    @DisplayName("postUlid 기반 조회 시 비로그인 사용자는 null uuid로 위임하는 활동 수행")
    void testGatherByPost_V1_givenAnonymousUser_willProcessAction() {
        // given
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), false);
        given(cacheService.getCacheData(any(), any(), any(PostId.class), any(), any())).willReturn(cacheData);
        given(controller.gatherByPost_V1(TEST_POST_ULID, null)).willReturn(List.of(testCommentOfPostResponse));

        // when
        restController.gatherByPost_V1(TEST_POST_ULID, testIfNoneMatch, testIfModifiedSince, null);

        // then
        then(controller).should(times(1)).gatherByPost_V1(TEST_POST_ULID, null);
    }

    @Test
    @DisplayName("postUlid 기반 커서 조회 시 캐시 가능한 경우 ResponseEntity 반환")
    void testGatherByPost_V2_givenCacheableResult_willReturnResponseEntity() {
        // given
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), true);
        given(cacheService.getCacheData(testIfNoneMatch, testIfModifiedSince, PostId.create(TEST_POST_ULID), null, TEST_COMMENT_PAGE_SIZE))
                .willReturn(cacheData);

        // when
        ResponseEntity<DataResponse<CommentCursorPageResponse>> result = restController.gatherByPost_V2(
                TEST_POST_ULID, null, TEST_COMMENT_PAGE_SIZE, testIfNoneMatch, testIfModifiedSince, null);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_MODIFIED);
        assertThat(result.getBody()).isNull();
        then(controller).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("postUlid 기반 커서 조회 시 캐시 불가능한 경우 ResponseEntity 반환")
    void testGatherByPost_V2_givenNotCacheableResultWithAuthenticatedMember_willReturnResponseEntity() {
        // given
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), false);
        given(cacheService.getCacheData(testIfNoneMatch, testIfModifiedSince, PostId.create(TEST_POST_ULID), TEST_COMMENT_PATH, TEST_COMMENT_PAGE_SIZE))
                .willReturn(cacheData);
        given(controller.gatherByPost_V2(TEST_POST_ULID, TEST_COMMENT_PATH, TEST_COMMENT_PAGE_SIZE, MEMBER_BASIC_USER_UUID))
                .willReturn(testCommentCursorPageResponse);
        DefaultUserDetails userDetails = DefaultUserDetails.builder().uuid(MEMBER_BASIC_USER_UUID).build();

        // when
        ResponseEntity<DataResponse<CommentCursorPageResponse>> result = restController.gatherByPost_V2(
                TEST_POST_ULID, TEST_COMMENT_PATH, TEST_COMMENT_PAGE_SIZE, testIfNoneMatch, testIfModifiedSince, userDetails);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(result.getBody());
        assertThat(result.getBody().getData()).isEqualTo(testCommentCursorPageResponse);
    }

    @Test
    @DisplayName("postUlid 기반 커서 조회 시 비로그인 사용자는 null uuid로 위임하는 활동 수행")
    void testGatherByPost_V2_givenAnonymousUser_willProcessAction() {
        // given
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), false);
        given(cacheService.getCacheData(any(), any(), any(PostId.class), any(), any())).willReturn(cacheData);
        given(controller.gatherByPost_V2(TEST_POST_ULID, null, TEST_COMMENT_PAGE_SIZE, null))
                .willReturn(testCommentCursorPageResponse);

        // when
        restController.gatherByPost_V2(TEST_POST_ULID, null, TEST_COMMENT_PAGE_SIZE, testIfNoneMatch, testIfModifiedSince, null);

        // then
        then(controller).should(times(1)).gatherByPost_V2(TEST_POST_ULID, null, TEST_COMMENT_PAGE_SIZE, null);
    }

    @Test
    @DisplayName("memberUuid 기반 조회 시 캐시 가능한 경우 ResponseEntity 반환")
    void testGatherByAuthor_givenCacheableResult_willReturnResponseEntity() {
        // given
        MemberId memberId = MemberId.fromUuid(MEMBER_BASIC_USER_UUID);
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), true);
        given(cacheService.getCacheData(testIfNoneMatch, testIfModifiedSince, memberId, 1, 8)).willReturn(cacheData);

        // when
        ResponseEntity<DataResponse<CommentOffsetPageResponse<CommentOfAuthorReadModel>>> result =
                restController.gatherByAuthor(MEMBER_BASIC_USER_UUID, 1, 8, testIfNoneMatch, testIfModifiedSince, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_MODIFIED);
        assertThat(result.getBody()).isNull();
        then(controller).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("memberUuid 기반 조회 시 캐시 불가능한 경우 ResponseEntity 반환")
    void testGatherByAuthor_givenNotCacheableResult_willReturnResponseEntity() {
        // given
        MemberId memberId = MemberId.fromUuid(MEMBER_BASIC_USER_UUID);
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), false);
        given(cacheService.getCacheData(testIfNoneMatch, testIfModifiedSince, memberId, 1, 8)).willReturn(cacheData);
        given(controller.gatherByAuthor(MEMBER_BASIC_USER_UUID, PageRequest.of(0, 8)))
                .willReturn(testCommentOffsetPageResponseOfAuthorPageModel);

        // when
        ResponseEntity<DataResponse<CommentOffsetPageResponse<CommentOfAuthorReadModel>>> result =
                restController.gatherByAuthor(MEMBER_BASIC_USER_UUID, 1, 8, testIfNoneMatch, testIfModifiedSince, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(result.getBody());
        assertThat(result.getBody().getData()).isEqualTo(testCommentOffsetPageResponseOfAuthorPageModel);
        then(controller).should(times(1)).gatherByAuthor(MEMBER_BASIC_USER_UUID, PageRequest.of(0, 8));
    }

    @Test
    @DisplayName("1-based page 파라미터를 0-based로 변환하여 위임하는 활동 수행")
    void testGatherByAuthor_givenSecondPage_willProcessAction() {
        // given
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), false);
        given(cacheService.getCacheData(any(), any(), any(MemberId.class), anyInt(), anyInt())).willReturn(cacheData);
        given(controller.gatherByAuthor(any(UUID.class), any())).willReturn(null);

        // when
        restController.gatherByAuthor(MEMBER_BASIC_USER_UUID, 2, 10, null, null, MEMBER_BASIC_USER_UUID);

        // then
        then(controller).should(times(1)).gatherByAuthor(MEMBER_BASIC_USER_UUID, PageRequest.of(1, 10));
    }

    @Test
    @DisplayName("본인이 아닌 회원의 댓글 조회 시 예외 반환")
    void testGatherByAuthor_givenNonSelfCaller_willThrowException() {
        // given & when & then
        assertThrows(AccessDeniedException.class,
                () -> restController.gatherByAuthor(MEMBER_BASIC_USER_UUID, 1, 8, null, null, MEMBER_BASIC_ADMIN_UUID));
    }

    @Test
    @DisplayName("유효한 댓글 등록 요청으로 ResponseEntity 반환")
    void testRegister_givenValidRegisterRequest_willReturnResponseEntity() {
        // given
        DefaultUserDetails userDetails = DefaultUserDetails.builder().uuid(MEMBER_BASIC_USER_UUID).build();
        doNothing().when(controller).register(testCommentRegisterRequest, MEMBER_BASIC_USER_UUID);

        // when
        ResponseEntity<DataResponse<Void>> result = restController.register(userDetails, testCommentRegisterRequest);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(controller).should(times(1)).register(testCommentRegisterRequest, MEMBER_BASIC_USER_UUID);
    }

    @Test
    @DisplayName("유효한 댓글 갱신 요청으로 ResponseEntity 반환")
    public void testUpdateContent_givenValidCommentUpdateRequest_willReturnResponseEntity() {
        // given
        DefaultUserDetails userDetails = DefaultUserDetails.builder().uuid(MEMBER_BASIC_USER_UUID).build();
        doNothing().when(controller).updateContent(testCommentUpdateRequest, MEMBER_BASIC_USER_UUID);

        // when
        ResponseEntity<DataResponse<Void>> result = restController.updateContent(userDetails, testCommentUpdateRequest);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(controller).should(times(1)).updateContent(testCommentUpdateRequest, MEMBER_BASIC_USER_UUID);
    }

    @Test
    @DisplayName("유효한 삭제 요청으로 ResponseEntity 반환")
    public void testDeleteComment_givenValidPathAndUlid_willReturnResponseEntity() {
        // given
        DefaultUserDetails userDetails = DefaultUserDetails.builder().uuid(MEMBER_BASIC_USER_UUID).build();
        doNothing().when(controller).delete(TEST_POST_ULID, "1", MEMBER_BASIC_USER_UUID);

        // when
        ResponseEntity<DataResponse<Void>> result = restController.deleteComment(userDetails, TEST_POST_ULID, "1");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(controller).should(times(1)).delete(TEST_POST_ULID, "1", MEMBER_BASIC_USER_UUID);
    }
}
