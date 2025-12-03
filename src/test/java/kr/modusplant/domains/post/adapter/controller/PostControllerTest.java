package kr.modusplant.domains.post.adapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kr.modusplant.domains.post.adapter.mapper.PostMapperImpl;
import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.common.util.usecase.model.PostReadModelTestUtils;
import kr.modusplant.domains.post.common.util.usecase.request.PostRequestTestUtils;
import kr.modusplant.domains.post.common.util.usecase.response.PostResponseTestUtils;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.AuthorId;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
import kr.modusplant.domains.post.usecase.port.processor.MultipartDataProcessorPort;
import kr.modusplant.domains.post.usecase.port.repository.*;
import kr.modusplant.domains.post.usecase.record.DraftPostReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.domains.post.usecase.request.PostCategoryRequest;
import kr.modusplant.domains.post.usecase.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.*;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

class PostControllerTest implements PostTestUtils, PostReadModelTestUtils, PostRequestTestUtils, PostResponseTestUtils {
    private final PostMapper postMapper = new PostMapperImpl();
    private final PostRepository postRepository = Mockito.mock(PostRepository.class);
    private final PostQueryRepository postQueryRepository = Mockito.mock(PostQueryRepository.class);
    private final PostQueryForMemberRepository postQueryForMemberRepository = Mockito.mock(PostQueryForMemberRepository.class);
    private final MultipartDataProcessorPort multipartDataProcessorPort = Mockito.mock(MultipartDataProcessorPort.class);
    private final PostViewCountRepository postViewCountRepository = Mockito.mock(PostViewCountRepository.class);
    private final PostViewLockRepository postViewLockRepository = Mockito.mock(PostViewLockRepository.class);
    private final PostArchiveRepository postArchiveRepository = Mockito.mock(PostArchiveRepository.class);
    private final PostRecentlyViewRepository postRecentlyViewRepository = Mockito.mock(PostRecentlyViewRepository.class);
    private final PostController postController = new PostController(postMapper, postRepository, postQueryRepository, postQueryForMemberRepository, multipartDataProcessorPort, postViewCountRepository,postViewLockRepository,postArchiveRepository, postRecentlyViewRepository);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postController, "ttlMinutes", 10L);
    }

    @Test
    @DisplayName("모든 발행된 게시글 조회")
    void testGetAll_givenCategoryAndCursor_willReturnCursorPageResponse() throws IOException {
        // given
        PostCategoryRequest categoryRequest = new PostCategoryRequest(testPrimaryCategoryId.getValue(), List.of(testSecondaryCategoryId.getValue()));
        UUID memberUuid = MEMBER_BASIC_USER_UUID;
        String ulid = TEST_POST_ULID;
        int size = 10;
        List<PostSummaryReadModel> readModels = List.of(TEST_POST_SUMMARY_READ_MODEL);

        given(postQueryRepository.findByCategoryWithCursor(testPrimaryCategoryId.getValue(), List.of(testSecondaryCategoryId.getValue()), memberUuid, ulid, size)).willReturn(readModels);
        given(multipartDataProcessorPort.convertToPreview(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);

        // when
        CursorPageResponse<PostSummaryResponse> result = postController.getAll(categoryRequest, memberUuid, ulid, size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.posts()).hasSize(1);
        assertThat(result.posts().get(0).ulid()).isEqualTo(TEST_POST_SUMMARY_READ_MODEL.ulid());
        assertThat(result.nextUlid()).isNull();
        assertThat(result.hasNext()).isFalse();

        verify(postQueryRepository).findByCategoryWithCursor(testPrimaryCategoryId.getValue(), List.of(testSecondaryCategoryId.getValue()), memberUuid, ulid, size);
        verify(multipartDataProcessorPort).convertToPreview(TEST_POST_SUMMARY_READ_MODEL.content());
    }

    @Test
    @DisplayName("키워드로 발행된 게시글 조회")
    void testGetByKeyword_givenKeywordAndCursor_willReturnCursorPageResponse() throws IOException {
        // given
        String keyword = "벌레";
        UUID memberUuid = MEMBER_BASIC_USER_UUID;
        String ulid = TEST_POST_ULID;
        int size = 10;
        List<PostSummaryReadModel> readModels = List.of(TEST_POST_SUMMARY_READ_MODEL);

        given(postQueryRepository.findByKeywordWithCursor(keyword, memberUuid, ulid, size)).willReturn(readModels);
        given(multipartDataProcessorPort.convertToPreview(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);

        // when
        CursorPageResponse<PostSummaryResponse> result = postController.getByKeyword(keyword, memberUuid, ulid, size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.posts()).hasSize(1);
        assertThat(result.posts().get(0).ulid()).isEqualTo(TEST_POST_SUMMARY_READ_MODEL.ulid());
        assertThat(result.nextUlid()).isNull();
        assertThat(result.hasNext()).isFalse();

        verify(postQueryRepository).findByKeywordWithCursor(keyword, memberUuid, ulid, size);
        verify(multipartDataProcessorPort).convertToPreview(TEST_POST_SUMMARY_READ_MODEL.content());
    }

    @Test
    @DisplayName("ULID로 발행된 게시글 상세 조회")
    void testGetByUlid_givenUlidAndMemberUuid_willReturnPostDetail() throws IOException {
        // given
        Long viewCount = 100L;

        given(postQueryRepository.findPostDetailByPostId(any(PostId.class), eq(MEMBER_BASIC_USER_UUID))).willReturn(Optional.of(TEST_PUBLISHED_POST_DETAIL_READ_MODEL));
        given(multipartDataProcessorPort.convertFileSrcToFullFileSrc(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);
        given(postViewLockRepository.lock(any(PostId.class), eq(MEMBER_BASIC_USER_UUID), anyLong())).willReturn(true);
        given(postViewCountRepository.increase(any(PostId.class))).willReturn(viewCount);
        doNothing().when(postRecentlyViewRepository).recordViewPost(any(UUID.class), any(PostId.class));
        given(postViewCountRepository.read(any(PostId.class))).willReturn(viewCount);

        // when
        PostDetailResponse result = postController.getByUlid(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.ulid()).isEqualTo(TEST_POST_ULID);
        verify(postQueryRepository).findPostDetailByPostId(any(PostId.class), eq(MEMBER_BASIC_USER_UUID));
        verify(multipartDataProcessorPort).convertFileSrcToFullFileSrc(any(JsonNode.class));
        verify(postViewLockRepository).lock(any(PostId.class), eq(MEMBER_BASIC_USER_UUID), anyLong());
        verify(postViewCountRepository).increase(any(PostId.class));
        verify(postRecentlyViewRepository).recordViewPost(eq(MEMBER_BASIC_USER_UUID), any(PostId.class));
        verify(postViewCountRepository).read(any(PostId.class));
    }

    /*@Test
    @DisplayName("작성자가 ULID로 임시저장 게시글 조회하기")
    void testGetByUlid_givenDraftPostAndAuthor_willReturnPostDetail() throws IOException {
        // given
        given(postQueryRepository.findPostDetailByPostId(any(PostId.class), eq(MEMBER_BASIC_USER_UUID))).willReturn(Optional.of(TEST_DRAFT_POST_DETAIL_READ_MODEL));
        given(multipartDataProcessorPort.convertFileSrcToFullFileSrc(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);

        // when
        PostDetailResponse result = postController.getByUlid(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).isNotNull();
        verify(postQueryRepository).findPostDetailByPostId(any(PostId.class), eq(MEMBER_BASIC_USER_UUID));
        verify(multipartDataProcessorPort).convertFileSrcToFullFileSrc(any(JsonNode.class));
        verify(postViewCountRepository, never()).read(any(PostId.class));
    }

    @Test
    @DisplayName("작성자가 아닐 때, ULID로 임시저장 게시글 조회 불가")
    void testGetByUlid_givenDraftPostAndNonAuthor_willReturnEmpty() throws IOException {
        // given
        UUID otherMemberUuid = UUID.randomUUID();

        given(postQueryRepository.findPostDetailByPostId(any(PostId.class), eq(otherMemberUuid))).willReturn(Optional.of(TEST_DRAFT_POST_DETAIL_READ_MODEL));

        // when & then
        assertThatThrownBy(() -> postController.getByUlid(TEST_POST_ULID, otherMemberUuid))
                .isInstanceOf(PostNotFoundException.class);
        verify(postQueryRepository).findPostDetailByPostId(any(PostId.class), eq(otherMemberUuid));
        verify(multipartDataProcessorPort, never()).convertFileSrcToFullFileSrc(any(JsonNode.class));
    }*/

    @Test
    @DisplayName("게시글 생성 및 발행")
    void testCreatePost_givenPublishedPostRequest_willCreatePost() throws IOException {
        // given
        given(multipartDataProcessorPort.saveFilesAndGenerateContentJson(anyList())).willReturn(TEST_POST_CONTENT_BINARY_DATA);

        // when
        postController.createPost(requestAllTypes, MEMBER_BASIC_USER_UUID);

        // then
        verify(multipartDataProcessorPort).saveFilesAndGenerateContentJson(anyList());
        verify(postRepository).save(argThat(post ->
                post.getAuthorId().getValue().equals(MEMBER_BASIC_USER_UUID) &&
                        post.getStatus().isPublished()
        ));
    }

    @Test
    @DisplayName("게시글 생성 및 임시저장")
    void testCreatePost_givenDraftPostRequest_willCreateDraftPost() throws IOException {
        // given
        given(multipartDataProcessorPort.saveFilesAndGenerateContentJson(anyList())).willReturn(TEST_POST_CONTENT_BINARY_DATA);

        // when
        postController.createPost(requestAllTypesDraft, MEMBER_BASIC_USER_UUID);

        // then
        verify(multipartDataProcessorPort).saveFilesAndGenerateContentJson(anyList());
        verify(postRepository).save(argThat(post ->
                post.getAuthorId().getValue().equals(MEMBER_BASIC_USER_UUID) &&
                        !post.getStatus().isPublished()
        ));
    }

    @Test
    @DisplayName("게시글 수정")
    void testUpdatePost_givenUpdateRequest_willUpdatePost() throws IOException {
        // given
        Post existingPost = createPublishedPost2();

        given(postRepository.getPostByUlid(any(PostId.class))).willReturn(Optional.of(existingPost));
        willDoNothing().given(multipartDataProcessorPort).deleteFiles(any(JsonNode.class));
        given(multipartDataProcessorPort.saveFilesAndGenerateContentJson(anyList())).willReturn(TEST_POST_CONTENT_BINARY_DATA);

        // when
        postController.updatePost(updateRequestAllTypes, MEMBER_BASIC_USER_UUID);

        // then
        verify(postRepository).getPostByUlid(any(PostId.class));
        verify(multipartDataProcessorPort).deleteFiles(any(JsonNode.class));
        verify(multipartDataProcessorPort).saveFilesAndGenerateContentJson(anyList());
        verify(postRepository).update(any(Post.class));
    }

    @Test
    @DisplayName("게시글 삭제 후 발행된 게시글은 아카이브에 저장")
    void testDeletePost_givenPublishedPost_willArchiveAndDelete() {
        // given
        Post existingPost = createPublishedPost2();

        given(postRepository.getPostByUlid(any(PostId.class))).willReturn(Optional.of(existingPost));
        willDoNothing().given(postArchiveRepository).save(any(PostId.class));
        willDoNothing().given(multipartDataProcessorPort).deleteFiles(any(JsonNode.class));
        willDoNothing().given(postRepository).delete(any(Post.class));

        // when
        postController.deletePost(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        verify(postRepository).getPostByUlid(any(PostId.class));
        verify(postArchiveRepository).save(any(PostId.class));
        verify(multipartDataProcessorPort).deleteFiles(any(JsonNode.class));
        verify(postRepository).delete(any(Post.class));
    }

    @Test
    @DisplayName("임시저장 게시글은 아카이브 없이 삭제")
    void testDeletePost_givenDraftPost_willDeleteWithoutArchive() {
        // given
        Post existingPost = createDraftPost2();

        given(postRepository.getPostByUlid(any(PostId.class)))
                .willReturn(Optional.of(existingPost));
        willDoNothing().given(multipartDataProcessorPort).deleteFiles(any(JsonNode.class));
        willDoNothing().given(postRepository).delete(any(Post.class));

        // when
        postController.deletePost(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        verify(postRepository).getPostByUlid(any(PostId.class));
        verify(postArchiveRepository, never()).save(any(PostId.class));
        verify(multipartDataProcessorPort).deleteFiles(any(JsonNode.class));
        verify(postRepository).delete(any(Post.class));
    }

    @Test
    @DisplayName("Redis에서 조회수 읽기")
    void testReadViewCount_givenUlidWithRedisCache_willReturnRedisValue() {
        // given
        Long redisViewCount = 150L;

        given(postViewCountRepository.read(any(PostId.class))).willReturn(redisViewCount);

        // when
        Long result = postController.readViewCount(TEST_POST_ULID);

        // then
        assertThat(result).isEqualTo(redisViewCount);
        verify(postViewCountRepository).read(any(PostId.class));
        verify(postRepository, never()).getViewCountByUlid(any(PostId.class));
    }

    @Test
    @DisplayName("Redis에 없다면 DB에서 조회 후 캐싱조회수 읽기")
    void testReadViewCount_givenUlidWithoutRedisCache_willReturnDbValueAndCache() {
        // given
        Long dbViewCount = 100L;

        given(postViewCountRepository.read(any(PostId.class))).willReturn(null);
        given(postRepository.getViewCountByUlid(any(PostId.class))).willReturn(dbViewCount);
        willDoNothing().given(postViewCountRepository).write(any(PostId.class), eq(dbViewCount));

        // when
        Long result = postController.readViewCount(TEST_POST_ULID);

        // then
        assertThat(result).isEqualTo(dbViewCount);
        verify(postViewCountRepository).read(any(PostId.class));
        verify(postRepository).getViewCountByUlid(any(PostId.class));
        verify(postViewCountRepository).write(any(PostId.class), eq(dbViewCount));
    }

    @Test
    @DisplayName("락 획득 성공 시 조회수 증가")
    void testIncreaseViewCount_givenUlidAndMemberWithoutLock_willIncreasViewCount() {
        // given
        long ttl = 10L;
        Long increasedViewCount = 101L;

        given(postViewLockRepository.lock(any(PostId.class), eq(MEMBER_BASIC_USER_UUID), eq(ttl))).willReturn(true);
        given(postViewCountRepository.increase(any(PostId.class))).willReturn(increasedViewCount);

        // when
        Long result = postController.increaseViewCount(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).isEqualTo(increasedViewCount);
        verify(postViewLockRepository).lock(any(PostId.class), eq(MEMBER_BASIC_USER_UUID), eq(ttl));
        verify(postViewCountRepository).increase(any(PostId.class));
    }

    @Test
    @DisplayName("락 획득 실패 시 기존 조회수 반환")
    void testIncreaseViewCount_givenUlidAndMemberWithExistingLock_willReturnCurrentViewCount() {
        // given
        long ttl = 10L;
        Long currentViewCount = 100L;

        given(postViewLockRepository.lock(any(PostId.class), eq(MEMBER_BASIC_USER_UUID), eq(ttl)))
                .willReturn(false);
        given(postViewCountRepository.read(any(PostId.class))).willReturn(currentViewCount);

        // when
        Long result = postController.increaseViewCount(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).isEqualTo(currentViewCount);
        verify(postViewLockRepository).lock(any(PostId.class), eq(MEMBER_BASIC_USER_UUID), eq(ttl));
        verify(postViewCountRepository).read(any(PostId.class));
        verify(postViewCountRepository, never()).increase(any(PostId.class));
    }

    @Test
    @DisplayName("특정 회원의 발행된 게시글 조회")
    void testGetByMemberUuid_givenMemberUuidAndPage_willReturnOffsetPageResponse() throws IOException {
        // given
        int page = 1;
        int size = 5;
        long totalElements = 1;
        Page<PostSummaryReadModel> readModelPage = new PageImpl<>(List.of(TEST_POST_SUMMARY_READ_MODEL), PageRequest.of(page-1,size),totalElements);

        given(postQueryForMemberRepository.findPublishedByAuthMemberWithOffset(any(AuthorId.class), eq(page-1), eq(size))).willReturn(readModelPage);
        given(multipartDataProcessorPort.convertToPreview(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);

        // when
        OffsetPageResponse<PostSummaryResponse> result = postController.getByMemberUuid(MEMBER_BASIC_USER_UUID, page-1, size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.posts()).hasSize(1);
        assertThat(result.posts().getFirst().ulid()).isEqualTo(TEST_POST_SUMMARY_READ_MODEL.ulid());
        assertThat(result.page()).isEqualTo(page);
        assertThat(result.totalElements()).isEqualTo(totalElements);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
        verify(postQueryForMemberRepository).findPublishedByAuthMemberWithOffset(any(AuthorId.class),eq(page-1),eq(size));
        verify(multipartDataProcessorPort).convertToPreview(any(JsonNode.class));
    }

    @Test
    @DisplayName("특정 회원의 임시저장 게시글 조회")
    void testGetDraftByMemberUuid_givenMemberUuidAndPage_willReturnOffsetPageResponse() throws IOException {
        // given
        int page = 1;
        int size = 5;
        long totalElements = 1;
        Page<DraftPostReadModel> readModelPage = new PageImpl<>(List.of(TEST_DRAFT_POST_READ_MODEL), PageRequest.of(page-1,size),totalElements);

        given(postQueryForMemberRepository.findDraftByAuthMemberWithOffset(any(AuthorId.class), eq(page-1),eq(size))).willReturn(readModelPage);
        given(multipartDataProcessorPort.convertToPreview(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);

        // when
        OffsetPageResponse<DraftPostResponse> result = postController.getDraftByMemberUuid(MEMBER_BASIC_USER_UUID,page-1,size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.posts()).hasSize(1);
        assertThat(result.posts().getFirst().ulid()).isEqualTo(TEST_DRAFT_POST_READ_MODEL.ulid());
        assertThat(result.page()).isEqualTo(page);
        assertThat(result.totalElements()).isEqualTo(totalElements);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();

        verify(postQueryForMemberRepository).findDraftByAuthMemberWithOffset(any(AuthorId.class), eq(page-1),eq(size));
        verify(multipartDataProcessorPort).convertToPreview(any(JsonNode.class));
    }

    @Test
    @DisplayName("최근 본 게시글 목록이 비어있을 때")
    void testGetRecentlyViewByMemberUuid_givenEmptyPostIds_willReturnEmptyPage() {
        // given
        long totalElements = 3L;
        int page = 1;
        int size = 5;

        given(postRecentlyViewRepository.getRecentlyViewPostIds(MEMBER_BASIC_USER_UUID, page-1, size)).willReturn(List.of());
        given(postRecentlyViewRepository.getTotalRecentlyViewPosts(MEMBER_BASIC_USER_UUID)).willReturn(totalElements);

        // when
        OffsetPageResponse<PostSummaryResponse> result = postController.getRecentlyViewByMemberUuid(MEMBER_BASIC_USER_UUID, page-1, size);

        // then
        assertThat(result.posts()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(totalElements);
        assertThat(result.page()).isEqualTo(page);
        assertThat(result.size()).isEqualTo(size);

        verify(postRecentlyViewRepository).getRecentlyViewPostIds(MEMBER_BASIC_USER_UUID,  page-1, size);
        verify(postRecentlyViewRepository).getTotalRecentlyViewPosts(MEMBER_BASIC_USER_UUID);
        verify(postQueryForMemberRepository, never()).findByIds(anyList(), any(UUID.class));
    }

    @Test
    @DisplayName("최근 본 게시글 목록이 있을 때")
    void testGetRecentlyViewByMemberUuid_givenPostIds_willReturnPostsPage() throws IOException {
        // given
        List<PostId> postIds = List.of(testPostId, testPostId2);
        long totalElements = 2L;
        int page = 1;
        int size = 5;

        List<PostSummaryReadModel> postModels = List.of(TEST_POST_SUMMARY_READ_MODEL, TEST_POST_SUMMARY_READ_MODEL2);
        List<PostSummaryResponse> postResponses = List.of(TEST_POST_SUMMARY_RESPONSE,TEST_POST_SUMMARY_RESPONSE2);

        given(postRecentlyViewRepository.getRecentlyViewPostIds(MEMBER_BASIC_USER_UUID, page-1, size)).willReturn(postIds);
        given(postRecentlyViewRepository.getTotalRecentlyViewPosts(MEMBER_BASIC_USER_UUID)).willReturn(totalElements);
        given(postQueryForMemberRepository.findByIds(postIds, MEMBER_BASIC_USER_UUID)).willReturn(postModels);
        given(multipartDataProcessorPort.convertToPreview(TEST_POST_CONTENT)).willReturn((ArrayNode) TEST_POST_CONTENT_PREVIEW);

        // when
        OffsetPageResponse<PostSummaryResponse> result = postController.getRecentlyViewByMemberUuid(MEMBER_BASIC_USER_UUID, page-1, size);

        // then
        assertThat(result.posts()).hasSize(2);
        assertThat(result.posts()).containsExactly(TEST_POST_SUMMARY_RESPONSE, TEST_POST_SUMMARY_RESPONSE2);
        assertThat(result.totalElements()).isEqualTo(totalElements);
        assertThat(result.page()).isEqualTo(page);
        assertThat(result.size()).isEqualTo(size);

        verify(postRecentlyViewRepository).getRecentlyViewPostIds(MEMBER_BASIC_USER_UUID, page-1, size);
        verify(postRecentlyViewRepository).getTotalRecentlyViewPosts(MEMBER_BASIC_USER_UUID);
        verify(postQueryForMemberRepository).findByIds(postIds, MEMBER_BASIC_USER_UUID);
        verify(multipartDataProcessorPort,times(2)).convertToPreview(TEST_POST_CONTENT);
    }

    @Test
    @DisplayName("특정 회원의 좋아요한 게시글 목록 조회")
    void testGetLikedByMemberUuid_givenMemberUuidAndPage_willReturnOffsetPageResponse() throws IOException {
        // given
        int page = 1;
        int size = 5;
        long totalElements = 1;
        Page<PostSummaryReadModel> readModelPage = new PageImpl<>(List.of(TEST_POST_SUMMARY_READ_MODEL), PageRequest.of(page-1,size),totalElements);

        given(postQueryForMemberRepository.findLikedByMemberWithOffset(eq(MEMBER_BASIC_USER_UUID), eq(page-1), eq(size))).willReturn(readModelPage);
        given(multipartDataProcessorPort.convertToPreview(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);

        // when
        OffsetPageResponse<PostSummaryResponse> result = postController.getLikedByMemberUuid(MEMBER_BASIC_USER_UUID,page-1,size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.posts()).hasSize(1);
        assertThat(result.posts().getFirst().ulid()).isEqualTo(TEST_POST_SUMMARY_READ_MODEL.ulid());
        assertThat(result.page()).isEqualTo(page);
        assertThat(result.totalElements()).isEqualTo(totalElements);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
        verify(postQueryForMemberRepository).findLikedByMemberWithOffset(eq(MEMBER_BASIC_USER_UUID),eq(page-1),eq(size));
        verify(multipartDataProcessorPort).convertToPreview(any(JsonNode.class));
    }

    @Test
    @DisplayName("특정 회원의 북마크한 게시글 목록 조회")
    void testGetBookmarkedByMemberUuid_givenMemberUuidAndPage_willReturnOffsetPageResponse() throws IOException {
        // given
        int page = 1;
        int size = 5;
        long totalElements = 1;
        Page<PostSummaryReadModel> readModelPage = new PageImpl<>(List.of(TEST_POST_SUMMARY_READ_MODEL), PageRequest.of(page-1,size),totalElements);

        given(postQueryForMemberRepository.findBookmarkedByMemberWithOffset(eq(MEMBER_BASIC_USER_UUID), eq(page-1), eq(size))).willReturn(readModelPage);
        given(multipartDataProcessorPort.convertToPreview(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);

        // when
        OffsetPageResponse<PostSummaryResponse> result = postController.getBookmarkedByMemberUuid(MEMBER_BASIC_USER_UUID,page-1,size);

        // then
        assertThat(result).isNotNull();
        assertThat(result.posts()).hasSize(1);
        assertThat(result.posts().getFirst().ulid()).isEqualTo(TEST_POST_SUMMARY_READ_MODEL.ulid());
        assertThat(result.page()).isEqualTo(page);
        assertThat(result.totalElements()).isEqualTo(totalElements);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
        verify(postQueryForMemberRepository).findBookmarkedByMemberWithOffset(eq(MEMBER_BASIC_USER_UUID),eq(page-1),eq(size));
        verify(multipartDataProcessorPort).convertToPreview(any(JsonNode.class));
    }

}