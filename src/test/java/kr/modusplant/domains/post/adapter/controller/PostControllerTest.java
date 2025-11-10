package kr.modusplant.domains.post.adapter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import kr.modusplant.domains.post.adapter.mapper.PostMapperImpl;
import kr.modusplant.domains.post.common.util.domain.aggregate.PostTestUtils;
import kr.modusplant.domains.post.common.util.usecase.model.PostReadModelTestUtils;
import kr.modusplant.domains.post.common.util.usecase.request.PostRequestTestUtils;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.vo.AuthorId;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.domain.vo.PrimaryCategoryId;
import kr.modusplant.domains.post.usecase.model.PostSummaryReadModel;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
import kr.modusplant.domains.post.usecase.port.processor.MultipartDataProcessorPort;
import kr.modusplant.domains.post.usecase.port.repository.PostArchiveRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostViewCountRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostViewLockRepository;
import kr.modusplant.domains.post.usecase.request.PostFilterRequest;
import kr.modusplant.domains.post.usecase.response.PostDetailResponse;
import kr.modusplant.domains.post.usecase.response.PostSummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT_BINARY_DATA;
import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class PostControllerTest implements PostTestUtils, PostReadModelTestUtils, PostRequestTestUtils {
    private final PostMapper postMapper = new PostMapperImpl();
    private final PostRepository postRepository = Mockito.mock(PostRepository.class);
    private final MultipartDataProcessorPort multipartDataProcessorPort = Mockito.mock(MultipartDataProcessorPort.class);
    private final PostViewCountRepository postViewCountRepository = Mockito.mock(PostViewCountRepository.class);
    private final PostViewLockRepository postViewLockRepository = Mockito.mock(PostViewLockRepository.class);
    private final PostArchiveRepository postArchiveRepository = Mockito.mock(PostArchiveRepository.class);
    private final PostController postController = new PostController(postMapper, postRepository, multipartDataProcessorPort, postViewCountRepository,postViewLockRepository,postArchiveRepository);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postController, "ttlMinutes", 10L);
    }

    @Test
    @DisplayName("모든 발행된 게시글 조회")
    void testGetAll_givenFilterAndPageable_willReturnPagedPostSummary() throws IOException {
        // given
        PostFilterRequest filterRequest = new PostFilterRequest(
                testPrimaryCategoryId.getValue(),
                List.of(testSecondaryCategoryId.getValue()),
                "식물"
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostSummaryReadModel> readModelPage = new PageImpl<>(List.of(TEST_POST_SUMMARY_READ_MODEL));

        given(postRepository.getPublishedPosts(
                any(PrimaryCategoryId.class),
                anyList(),
                eq("식물"),
                eq(pageable)
        )).willReturn(readModelPage);
        given(multipartDataProcessorPort.convertToPreviewData(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);

        // when
        Page<PostSummaryResponse> result = postController.getAll(filterRequest, pageable);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).ulid()).isEqualTo(TEST_POST_SUMMARY_READ_MODEL.ulid());
        verify(postRepository).getPublishedPosts(
                any(PrimaryCategoryId.class),
                anyList(),
                eq("식물"),
                eq(pageable)
        );
        verify(multipartDataProcessorPort).convertToPreviewData(any(JsonNode.class));
    }

    @Test
    @DisplayName("특정 회원의 발행된 게시글 조회")
    void testGetByMemberUuid_givenMemberUuidAndFilter_willReturnPagedPostSummary() throws IOException {
        // given
        PostFilterRequest filterRequest = new PostFilterRequest(
                testPrimaryCategoryId.getValue(),
                List.of(testSecondaryCategoryId.getValue()),
                "식물"
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostSummaryReadModel> readModelPage = new PageImpl<>(List.of(TEST_POST_SUMMARY_READ_MODEL));

        given(postRepository.getPublishedPostsByAuthor(
                any(AuthorId.class),
                any(PrimaryCategoryId.class),
                anyList(),
                anyString(),
                eq(pageable)
        )).willReturn(readModelPage);
        given(multipartDataProcessorPort.convertToPreviewData(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);

        // when
        Page<PostSummaryResponse> result = postController.getByMemberUuid(MEMBER_BASIC_USER_UUID, filterRequest, pageable);

        // then
        assertThat(result).hasSize(1);
        verify(postRepository).getPublishedPostsByAuthor(
                any(AuthorId.class),
                any(PrimaryCategoryId.class),
                anyList(),
                anyString(),
                eq(pageable)
        );
        verify(multipartDataProcessorPort).convertToPreviewData(any(JsonNode.class));
    }

    @Test
    @DisplayName("특정 회원의 임시저장 게시글 조회")
    void testGetDraftByMemberUuid_givenMemberUuid_willReturnPagedDraftPosts() throws IOException {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<PostSummaryReadModel> readModelPage = new PageImpl<>(List.of(TEST_POST_SUMMARY_READ_MODEL));

        given(postRepository.getDraftPostsByAuthor(any(AuthorId.class), eq(pageable))).willReturn(readModelPage);
        given(multipartDataProcessorPort.convertToPreviewData(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);

        // when
        Page<PostSummaryResponse> result = postController.getDraftByMemberUuid(MEMBER_BASIC_USER_UUID, pageable);

        // then
        assertThat(result).hasSize(1);
        verify(postRepository).getDraftPostsByAuthor(any(AuthorId.class), eq(pageable));
        verify(multipartDataProcessorPort).convertToPreviewData(any(JsonNode.class));
    }

    @Test
    @DisplayName("ULID로 발행된 게시글 상세 조회")
    void testGetByUlid_givenUlidAndMemberUuid_willReturnPostDetail() throws IOException {
        // given
        Long viewCount = 100L;

        given(postRepository.getPostDetailByUlid(any(PostId.class))).willReturn(Optional.of(TEST_PUBLISHED_POST_DETAIL_READ_MODEL));
        given(multipartDataProcessorPort.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);
        given(postViewCountRepository.read(any(PostId.class))).willReturn(viewCount);

        // when
        Optional<PostDetailResponse> result = postController.getByUlid(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().ulid()).isEqualTo(TEST_POST_ULID);
        verify(postRepository).getPostDetailByUlid(any(PostId.class));
        verify(multipartDataProcessorPort).convertFileSrcToBinaryData(any(JsonNode.class));
        verify(postViewCountRepository).read(any(PostId.class));
    }

    @Test
    @DisplayName("작성자가 ULID로 임시저장 게시글 조회하기")
    void testGetByUlid_givenDraftPostAndAuthor_willReturnPostDetail() throws IOException {
        // given
        given(postRepository.getPostDetailByUlid(any(PostId.class))).willReturn(Optional.of(TEST_DRAFT_POST_DETAIL_READ_MODEL));
        given(multipartDataProcessorPort.convertFileSrcToBinaryData(any(JsonNode.class))).willReturn((ArrayNode) TEST_POST_CONTENT_BINARY_DATA);

        // when
        Optional<PostDetailResponse> result = postController.getByUlid(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).isPresent();
        verify(postRepository).getPostDetailByUlid(any(PostId.class));
        verify(multipartDataProcessorPort).convertFileSrcToBinaryData(any(JsonNode.class));
        verify(postViewCountRepository, never()).read(any(PostId.class));
    }

    @Test
    @DisplayName("작성자가 아닐 때, ULID로 임시저장 게시글 조회 불가")
    void testGetByUlid_givenDraftPostAndNonAuthor_willReturnEmpty() {
        // given
        UUID otherMemberUuid = UUID.randomUUID();

        given(postRepository.getPostDetailByUlid(any(PostId.class))).willReturn(Optional.of(TEST_DRAFT_POST_DETAIL_READ_MODEL));

        // when
        Optional<PostDetailResponse> result = postController.getByUlid(TEST_POST_ULID, otherMemberUuid);

        // then
        assertThat(result).isEmpty();
        verify(postRepository).getPostDetailByUlid(any(PostId.class));
    }

    @Test
    @DisplayName("게시글 생성 및 발행")
    void testCreatePost_givenPublishedPostRequest_willCreatePost() throws IOException {
        // given
        given(multipartDataProcessorPort.saveFilesAndGenerateContentJson(anyList())).willReturn(TEST_POST_CONTENT_BINARY_DATA);
        given(postRepository.save(any(Post.class))).willReturn(TEST_PUBLISHED_POST_DETAIL_READ_MODEL);

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
        given(postRepository.save(any(Post.class))).willReturn(TEST_DRAFT_POST_DETAIL_READ_MODEL);

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
        given(postRepository.save(any(Post.class))).willReturn(TEST_PUBLISHED_POST_DETAIL_READ_MODEL);

        // when
        postController.updatePost(updateRequestAllTypes, MEMBER_BASIC_USER_UUID);

        // then
        verify(postRepository).getPostByUlid(any(PostId.class));
        verify(multipartDataProcessorPort).deleteFiles(any(JsonNode.class));
        verify(multipartDataProcessorPort).saveFilesAndGenerateContentJson(anyList());
        verify(postRepository).save(any(Post.class));
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

}