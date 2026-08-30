package kr.modusplant.domains.comment.framework.inbound.web.rest;

import kr.modusplant.domains.comment.adapter.controller.CommentController;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.request.CommentDeleteRequestTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.request.CommentRegisterRequestTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.request.CommentUpdateRequestTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.response.CommentOfPostResponseTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.response.CommentPageResponseTestUtils;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.inbound.web.cache.CommentCacheService;
import kr.modusplant.domains.comment.framework.inbound.web.cache.model.CommentCacheData;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentPageResponse;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

public class CommentRestControllerTest implements PostIdTestUtils,
        CommentOfPostResponseTestUtils, CommentRegisterRequestTestUtils,
        CommentUpdateRequestTestUtils, CommentDeleteRequestTestUtils, MemberIdTestUtils,
        CommentPageResponseTestUtils {
    private final CommentController controller = Mockito.mock(CommentController.class);
    private final CommentCacheService cacheService = Mockito.mock(CommentCacheService.class);
    private final CommentRestController restController = new CommentRestController(controller, cacheService);

    private final String testIfNoneMatch = "\"abc123\"";
    private final String testIfModifiedSince = "Sat, 01 Jan 2025 00:00:00 GMT";

    @Test
    @DisplayName("postUlid 기반 조회 시 캐시 가능한 경우 304와 빈 본문 반환")
    void testGatherByPost_givenCacheableResult_willReturnNotModified() {
        // given
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), true);
        given(cacheService.getCacheData(testIfNoneMatch, testIfModifiedSince, PostId.create(TEST_POST_ULID)))
                .willReturn(cacheData);

        // when
        ResponseEntity<DataResponse<List<CommentOfPostResponse>>> result =
                restController.gatherByPost(TEST_POST_ULID, testIfNoneMatch, testIfModifiedSince, null);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_MODIFIED);
        assertThat(result.getBody()).isNull();
        then(controller).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("postUlid 기반 조회 시 캐시 불가능한 경우 controller에 위임하여 200과 본문 반환")
    void testGatherByPost_givenNotCacheableResultWithAuthenticatedMember_willDelegateToController() {
        // given
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), false);
        given(cacheService.getCacheData(testIfNoneMatch, testIfModifiedSince, PostId.create(TEST_POST_ULID)))
                .willReturn(cacheData);
        given(controller.gatherByPost(TEST_POST_ULID, MEMBER_BASIC_USER_UUID))
                .willReturn(List.of(testCommentOfPostResponse));
        DefaultUserDetails userDetails = DefaultUserDetails.builder().uuid(MEMBER_BASIC_USER_UUID).build();

        // when
        ResponseEntity<DataResponse<List<CommentOfPostResponse>>> result =
                restController.gatherByPost(TEST_POST_ULID, testIfNoneMatch, testIfModifiedSince, userDetails);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(result.getBody());
        assertThat(result.getBody().getData()).isEqualTo(List.of(testCommentOfPostResponse));
    }

    @Test
    @DisplayName("postUlid 기반 조회 시 비로그인 사용자는 null uuid로 위임됨")
    void testGatherByPost_givenAnonymousUser_willDelegateWithNullUuid() {
        // given
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), false);
        given(cacheService.getCacheData(any(), any(), any(PostId.class))).willReturn(cacheData);
        given(controller.gatherByPost(TEST_POST_ULID, null)).willReturn(List.of(testCommentOfPostResponse));

        // when
        restController.gatherByPost(TEST_POST_ULID, testIfNoneMatch, testIfModifiedSince, null);

        // then
        then(controller).should(times(1)).gatherByPost(TEST_POST_ULID, null);
    }

    @Test
    @DisplayName("memberUuid 기반 조회 시 캐시 가능한 경우 304와 빈 본문 반환")
    void testGatherByAuthor_givenCacheableResult_willReturnNotModified() {
        // given
        MemberId memberId = MemberId.fromUuid(MEMBER_BASIC_USER_UUID);
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), true);
        given(cacheService.getCacheData(testIfNoneMatch, testIfModifiedSince, memberId)).willReturn(cacheData);

        // when
        ResponseEntity<DataResponse<CommentPageResponse<CommentOfAuthorReadModel>>> result =
                restController.gatherByAuthor(MEMBER_BASIC_USER_UUID, 1, 8, testIfNoneMatch, testIfModifiedSince);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_MODIFIED);
        assertThat(result.getBody()).isNull();
        then(controller).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("memberUuid 기반 조회 시 캐시 불가능한 경우 controller에 위임하여 200과 본문 반환")
    void testGatherByAuthor_givenNotCacheableResult_willDelegateToController() {
        // given
        MemberId memberId = MemberId.fromUuid(MEMBER_BASIC_USER_UUID);
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), false);
        given(cacheService.getCacheData(testIfNoneMatch, testIfModifiedSince, memberId)).willReturn(cacheData);
        given(controller.gatherByAuthor(MEMBER_BASIC_USER_UUID, PageRequest.of(0, 8)))
                .willReturn(testCommentPageResponseOfAuthorPageModel);

        // when
        ResponseEntity<DataResponse<CommentPageResponse<CommentOfAuthorReadModel>>> result =
                restController.gatherByAuthor(MEMBER_BASIC_USER_UUID, 1, 8, testIfNoneMatch, testIfModifiedSince);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(result.getBody());
        assertThat(result.getBody().getData()).isEqualTo(testCommentPageResponseOfAuthorPageModel);
        then(controller).should(times(1)).gatherByAuthor(MEMBER_BASIC_USER_UUID, PageRequest.of(0, 8));
    }

    @Test
    @DisplayName("1-based page 파라미터가 0-based로 변환되어 controller에 위임됨")
    void testGatherByAuthor_givenSecondPage_willConvertToZeroBasedPageIndex() {
        // given
        CommentCacheData cacheData = new CommentCacheData("etag", LocalDateTime.now(), false);
        given(cacheService.getCacheData(any(), any(), any(MemberId.class))).willReturn(cacheData);
        given(controller.gatherByAuthor(any(UUID.class), any())).willReturn(null);

        // when
        restController.gatherByAuthor(MEMBER_BASIC_USER_UUID, 2, 10, null, null);

        // then
        then(controller).should(times(1)).gatherByAuthor(MEMBER_BASIC_USER_UUID, PageRequest.of(1, 10));
    }

    @Test
    @DisplayName("유효한 댓글 등록 요청으로 controller.register 호출 후 200 반환")
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
    @DisplayName("유효한 댓글 갱신 요청 객체로 댓글 갱신하기")
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
    @DisplayName("유효한 삭제 요청으로 controller.delete 호출 후 200 반환")
    public void testDelete_givenValidDeleteRequest_willReturnResponseEntity() {
        // given
        DefaultUserDetails userDetails = DefaultUserDetails.builder().uuid(MEMBER_BASIC_USER_UUID).build();
        doNothing().when(controller).delete(testCommentDeleteRequest, MEMBER_BASIC_USER_UUID);

        // when
        ResponseEntity<DataResponse<Void>> result = restController.delete(userDetails, testCommentDeleteRequest);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(controller).should(times(1)).delete(testCommentDeleteRequest, MEMBER_BASIC_USER_UUID);
    }
}
