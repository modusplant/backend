package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.mapper.CommentMapperImpl;
import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.common.util.usecase.response.CommentResponseTestUtils;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.framework.in.web.cache.CommentCacheService;
import kr.modusplant.domains.comment.framework.in.web.cache.model.CommentCacheData;
import kr.modusplant.domains.comment.framework.out.persistence.jooq.CommentJooqRepository;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentRepositoryJpaAdapter;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorPageModel;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import kr.modusplant.domains.comment.usecase.port.outbound.CommentPostValidator;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.usecase.request.CommentUpdateRequest;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentPageResponse;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.framework.jpa.entity.common.util.CommCommentIdTestUtils;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.swear.service.SwearService;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import kr.modusplant.shared.persistence.constant.TableName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static kr.modusplant.domains.comment.common.util.usecase.model.CommentOfAuthorPageModelTestUtils.testCommentOfAuthorPageModel;
import static kr.modusplant.domains.comment.common.util.usecase.model.CommentOfPostReadModelTestUtils.testCommentOfPostReadModel;
import static kr.modusplant.domains.comment.common.util.usecase.response.CommentPageResponseTestUtils.testCommentPageResponseOfAuthorPageModel;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;

public class CommentControllerTest implements PostIdTestUtils, AuthorTestUtils,
        CommentResponseTestUtils, CommCommentIdTestUtils {
    private final CommentMapperImpl mapper = Mockito.mock(CommentMapperImpl.class);
    private final CommentJooqRepository readRepository = Mockito.mock(CommentJooqRepository.class);
    private final CommentRepositoryJpaAdapter writeRepository = Mockito.mock(CommentRepositoryJpaAdapter.class);
    private final CommPostJpaRepository postJpaRepository = Mockito.mock(CommPostJpaRepository.class);
    private final SiteMemberJpaRepository memberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final SwearService swearService = Mockito.mock(SwearService.class);
    private final CommentPostValidator postValidator = Mockito.mock(CommentPostValidator.class);
    private final CommentCacheService cacheService = Mockito.mock(CommentCacheService.class);
    private final CommentController controller = new CommentController(mapper, readRepository,
            writeRepository, postJpaRepository, memberJpaRepository, swearService, postValidator, cacheService);

    private final String testIfNoneMatch = "\"abc123\"";
    private final String testIfModifiedSince = "Sat, 01 Jan 2025 00:00:00 GMT";

    @Test
    @DisplayName("postUlid 기반 캐시 데이터 조회 성공")
    void testGetCacheDataByPost_givenValidPostUlid_willReturnCacheData() {
        // given
        CommentCacheData expected = Mockito.mock(CommentCacheData.class);
        given(cacheService.getCacheData(
                eq(testIfNoneMatch),
                eq(testIfModifiedSince),
                eq(PostId.create(TEST_COMM_POST_ULID))))
                .willReturn(expected);

        // when
        CommentCacheData result =
                controller.getCacheData(TEST_COMM_POST_ULID, testIfNoneMatch, testIfModifiedSince);

        // then
        assertThat(result).isEqualTo(expected);
        then(cacheService).should(times(1))
                .getCacheData(testIfNoneMatch, testIfModifiedSince, PostId.create(TEST_COMM_POST_ULID));
    }

    @Test
    @DisplayName("postUlid 기반 캐시 조회 시 cacheService 예외가 그대로 전파")
    void testGetCacheDataByPost_whenCacheServiceThrows_willPropagateException() {
        // given
        given(cacheService.getCacheData(any(), any(), any(PostId.class)))
                .willThrow(new RuntimeException("cache error"));

        // when / then
        assertThrows(RuntimeException.class,
                () -> controller.getCacheData(TEST_COMM_POST_ULID, testIfNoneMatch, testIfModifiedSince));
    }

    @Test
    @DisplayName("memberUuid 기반 캐시 데이터 조회 성공")
    void testGetCacheDataByMember_givenValidMemberUuid_willReturnCacheData() {
        // given
        CommentCacheData expected = Mockito.mock(CommentCacheData.class);
        given(cacheService.getCacheData(
                eq(testIfNoneMatch),
                eq(testIfModifiedSince),
                eq(MemberId.fromUuid(MEMBER_BASIC_USER_UUID))))
                .willReturn(expected);

        // when
        CommentCacheData result =
                controller.getCacheData(MEMBER_BASIC_USER_UUID, testIfNoneMatch, testIfModifiedSince);

        // then
        assertThat(result).isEqualTo(expected);
        then(cacheService).should(times(1))
                .getCacheData(testIfNoneMatch, testIfModifiedSince, MemberId.fromUuid(MEMBER_BASIC_USER_UUID));
    }

    @Test
    @DisplayName("memberUuid 기반 캐시 조회 시 cacheService 예외가 그대로 전파")
    void testGetCacheDataByMember_whenCacheServiceThrows_willPropagateException() {
        // given
        given(cacheService.getCacheData(any(), any(), any(MemberId.class)))
                .willThrow(new RuntimeException("cache error"));

        // when / then
        assertThrows(RuntimeException.class,
                () -> controller.getCacheData(MEMBER_BASIC_USER_UUID, testIfNoneMatch, testIfModifiedSince));
    }

    @Test
    @DisplayName("존재하지 않는 postUlid로 조회 시 NotFoundEntityException 발생")
    void testGatherByPost_givenNonExistentPostUlid_willThrowNotFoundEntityException() {
        // given
        given(postJpaRepository.existsByUlid(TEST_COMM_POST_ULID)).willReturn(false);

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> controller.gatherByPost(TEST_COMM_POST_ULID, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_POST);
        assertThat(ex.getEntityName()).isEqualTo("post");
    }

    @Test
    @DisplayName("유효한 postUlid와 로그인 사용자로 댓글 목록 조회 성공")
    void testGatherByPost_givenValidPostUlidWithAuthenticatedMember_willReturnCommentList() {
        // given
        CommentOfPostResponse mockResponse = Mockito.mock(CommentOfPostResponse.class);
        List<CommentOfPostReadModel> testPostReadModel = List.of(testCommentOfPostReadModel);

        given(postJpaRepository.existsByUlid(TEST_COMM_POST_ULID)).willReturn(true);
        given(readRepository.findByPost(
                eq(PostId.create(TEST_COMM_POST_ULID)),
                eq(Author.createNullable(MEMBER_BASIC_USER_UUID))))
                .willReturn(testPostReadModel);
        given(mapper.toCommentOfPostResponse(testCommentOfPostReadModel)).willReturn(mockResponse);

        // when
        List<CommentOfPostResponse> result =
                controller.gatherByPost(TEST_COMM_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockResponse);
        then(readRepository).should(times(1))
                .findByPost(PostId.create(TEST_COMM_POST_ULID), Author.createNullable(MEMBER_BASIC_USER_UUID));
    }

    @Test
    @DisplayName("유효한 postUlid와 비로그인 사용자(null)로 댓글 목록 조회 성공")
    void testGatherByPost_givenValidPostUlidWithAnonymousMember_willReturnCommentList() {
        // given
        CommentOfPostResponse mockResponse = Mockito.mock(CommentOfPostResponse.class);
        List<CommentOfPostReadModel> testPostReadModel = List.of(testCommentOfPostReadModel);

        given(postJpaRepository.existsByUlid(TEST_COMM_POST_ULID)).willReturn(true);
        given(readRepository.findByPost(
                eq(PostId.create(TEST_COMM_POST_ULID)),
                eq(Author.createNullable(null))))
                .willReturn(testPostReadModel);
        given(mapper.toCommentOfPostResponse(testCommentOfPostReadModel)).willReturn(mockResponse);

        // when
        List<CommentOfPostResponse> result = controller.gatherByPost(TEST_COMM_POST_ULID, null);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockResponse);
    }

    @Test
    @DisplayName("게시글에 댓글이 없을 경우 빈 리스트 반환")
    void testGatherByPost_givenPostWithNoComments_willReturnEmptyList() {
        // given
        given(postJpaRepository.existsByUlid(TEST_COMM_POST_ULID)).willReturn(true);
        given(readRepository.findByPost(any(PostId.class), any(Author.class)))
                .willReturn(Collections.emptyList());

        // when
        List<CommentOfPostResponse> result =
                controller.gatherByPost(TEST_COMM_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).isEmpty();
        then(mapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 memberUuid로 조회 시 NotFoundEntityException 발생")
    void testGatherByAuthor_givenNonExistentMemberUuid_willThrowNotFoundEntityException() {
        // given
        given(memberJpaRepository.existsById(MEMBER_BASIC_USER_UUID)).willReturn(false);

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> controller.gatherByAuthor(MEMBER_BASIC_USER_UUID, Pageable.unpaged()));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_MEMBER);
        assertThat(ex.getEntityName()).isEqualTo("member");
    }

    @Test
    @DisplayName("유효한 memberUuid로 페이지 댓글 조회 성공 - 응답 필드 검증")
    void testGatherByAuthor_givenValidMemberUuid_willReturnMappedPageResponse() {
        // given
        Pageable pageable = PageRequest.of(0, 1);
        List<CommentOfAuthorPageModel> content = List.of(testCommentOfAuthorPageModel);
        PageImpl<CommentOfAuthorPageModel> page =
                new PageImpl<>(content, pageable, 1L);

        given(memberJpaRepository.existsById(MEMBER_BASIC_USER_UUID)).willReturn(true);
        given(readRepository.findByAuthor(Author.create(MEMBER_BASIC_USER_UUID), pageable))
                .willReturn(page);

        // when
        CommentPageResponse<CommentOfAuthorPageModel> response =
                controller.gatherByAuthor(MEMBER_BASIC_USER_UUID, pageable);

        // then
        assertThat(response.getCommentList()).isEqualTo(testCommentPageResponseOfAuthorPageModel.getCommentList());
        assertThat(response.getPage()).isEqualTo(testCommentPageResponseOfAuthorPageModel.getPage());
        assertThat(response.getSize()).isEqualTo(testCommentPageResponseOfAuthorPageModel.getSize());
        assertThat(response.getTotalElements()).isEqualTo(testCommentPageResponseOfAuthorPageModel.getTotalElements());
        assertThat(response.getTotalPages()).isEqualTo(testCommentPageResponseOfAuthorPageModel.getTotalPages());
        assertThat(response.isHasNext()).isEqualTo(testCommentPageResponseOfAuthorPageModel.isHasNext());
        assertThat(response.isHasPrevious()).isEqualTo(testCommentPageResponseOfAuthorPageModel.isHasPrevious());
    }

    @Test
    @DisplayName("gatherByAuthor 반환 페이지 번호는 1-based index 적용됨")
    void testGatherByAuthor_givenSecondPage_willApplyOneIndexedPageNumber() {
        // given
        Pageable pageable = PageRequest.of(1, 10); // 0-based page 1
        PageImpl<CommentOfAuthorPageModel> page =
                new PageImpl<>(Collections.emptyList(), pageable, 25L);

        given(memberJpaRepository.existsById(MEMBER_BASIC_USER_UUID)).willReturn(true);
        given(readRepository.findByAuthor(Author.create(MEMBER_BASIC_USER_UUID), pageable))
                .willReturn(page);

        // when
        CommentPageResponse<CommentOfAuthorPageModel> response =
                controller.gatherByAuthor(MEMBER_BASIC_USER_UUID, pageable);

        // then
        // 0-based index 1 → 1-based index 2
        assertThat(response.getPage()).isEqualTo(2);
    }

    @Test
    @DisplayName("gatherByAuthor - 결과가 없는 페이지 조회 시 빈 컨텐츠 반환")
    void testGatherByAuthor_givenPageWithNoResults_willReturnEmptyContent() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<CommentOfAuthorPageModel> emptyPage =
                new PageImpl<>(Collections.emptyList(), pageable, 0L);

        given(memberJpaRepository.existsById(MEMBER_BASIC_USER_UUID)).willReturn(true);
        given(readRepository.findByAuthor(Author.create(MEMBER_BASIC_USER_UUID), pageable))
                .willReturn(emptyPage);

        // when
        CommentPageResponse<CommentOfAuthorPageModel> response =
                controller.gatherByAuthor(MEMBER_BASIC_USER_UUID, pageable);

        // then
        assertThat(response.getCommentList()).isEmpty();
        assertThat(response.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("이미 존재하는 댓글 경로로 등록 시 InvalidValueException 발생")
    void testRegister_givenExistingCommentPath_willThrowInvalidValueException() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_COMM_POST_ULID, "1", "content");
        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("1")))
                .willReturn(true);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.EXIST_COMMENT);
    }

    @Test
    @DisplayName("게시되지 않은 게시글에 댓글 등록 시 InvalidValueException 발생")
    void testRegister_givenUnpublishedPost_willThrowInvalidValueException() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_COMM_POST_ULID, "1", "content");
        given(readRepository.existsByPostAndPath(any(), any())).willReturn(false);
        given(postValidator.isPostPublished(TEST_COMM_POST_ULID)).willReturn(false);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_PUBLISHED_POST);
    }

    @Test
    @DisplayName("경로가 '1'이고 게시글에 이미 댓글이 있는 경우 InvalidValueException 발생")
    void testRegister_givenPathIsOneButPostAlreadyHasComment_willThrowInvalidValueException() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_COMM_POST_ULID, "1", "content");
        given(readRepository.existsByPostAndPath(any(), any())).willReturn(false);
        given(postValidator.isPostPublished(TEST_COMM_POST_ULID)).willReturn(true);
        given(readRepository.countPostComment(PostId.create(TEST_COMM_POST_ULID))).willReturn(1);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.EXIST_POST_COMMENT);
    }

    @Test
    @DisplayName("경로가 '1'이고 게시글에 댓글이 없는 경우 최상위 댓글 등록 성공")
    void testRegister_givenPathIsOneAndPostHasNoComment_willSaveComment() {
        // given
        String content = "hello";
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_COMM_POST_ULID, "1", content);
        Comment mockComment = Mockito.mock(Comment.class);

        given(readRepository.existsByPostAndPath(any(), any())).willReturn(false);
        given(postValidator.isPostPublished(TEST_COMM_POST_ULID)).willReturn(true);
        given(readRepository.countPostComment(PostId.create(TEST_COMM_POST_ULID))).willReturn(0);
        given(swearService.filterSwear(content)).willReturn(content);
        given(mapper.toComment(any(), any(), any(), any())).willReturn(mockComment);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(writeRepository).should(times(1)).save(mockComment);
    }

    @Test
    @DisplayName("경로에 '.'이 없고 1이 아닌 경우 - 형제 댓글이 없으면 InvalidValueException 발생")
    void testRegister_givenRootLevelPathWithNoSiblingComment_willThrowInvalidValueException() {
        // given: path "3" requires path "2" to exist
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_COMM_POST_ULID, "3", "content");
        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("3")))
                .willReturn(false);
        given(postValidator.isPostPublished(TEST_COMM_POST_ULID)).willReturn(true);
        // sibling path "2" does not exist
        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("2")))
                .willReturn(false);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_EXIST_SIBLING_COMMENT);
    }

    @Test
    @DisplayName("경로에 '.'이 없고 1이 아닌 경우 - 형제 댓글이 있으면 등록 성공")
    void testRegister_givenRootLevelPathWithExistingSiblingComment_willSaveComment() {
        // given: path "3" requires path "2" to exist
        String content = "hello";
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_COMM_POST_ULID, "3", content);
        Comment mockComment = Mockito.mock(Comment.class);

        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("3")))
                .willReturn(false);
        given(postValidator.isPostPublished(TEST_COMM_POST_ULID)).willReturn(true);
        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("2")))
                .willReturn(true);
        given(swearService.filterSwear(content)).willReturn(content);
        given(mapper.toComment(any(), any(), any(), any())).willReturn(mockComment);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(writeRepository).should(times(1)).save(mockComment);
    }

    @Test
    @DisplayName("중첩 경로 마지막 숫자가 '1'일 때 - 부모 댓글이 없으면 InvalidValueException 발생")
    void testRegister_givenNestedPathEndingInOneWithNoParent_willThrowInvalidValueException() {
        // given: path "1.2.1" requires parent "1.2" to exist
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_COMM_POST_ULID, "1.2.1", "content");
        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("1.2.1")))
                .willReturn(false);
        given(postValidator.isPostPublished(TEST_COMM_POST_ULID)).willReturn(true);
        // parent "1.2" does not exist
        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("1.2")))
                .willReturn(false);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_EXIST_PARENT_COMMENT);
    }

    @Test
    @DisplayName("중첩 경로 마지막 숫자가 '1'일 때 - 부모 댓글이 있으면 등록 성공")
    void testRegister_givenNestedPathEndingInOneWithExistingParent_willSaveComment() {
        // given: path "1.2.1" requires parent "1.2" to exist
        String content = "reply";
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_COMM_POST_ULID, "1.2.1", content);
        Comment mockComment = Mockito.mock(Comment.class);

        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("1.2.1")))
                .willReturn(false);
        given(postValidator.isPostPublished(TEST_COMM_POST_ULID)).willReturn(true);
        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("1.2")))
                .willReturn(true);
        given(swearService.filterSwear(content)).willReturn(content);
        given(mapper.toComment(any(), any(), any(), any())).willReturn(mockComment);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(writeRepository).should(times(1)).save(mockComment);
    }

    @Test
    @DisplayName("중첩 경로 마지막 숫자가 1 초과일 때 - 형제 댓글이 없으면 InvalidValueException 발생")
    void testRegister_givenNestedPathWithNoSiblingComment_willThrowInvalidValueException() {
        // given: path "1.5.3" requires sibling "1.5.2" to exist
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_COMM_POST_ULID, "1.5.3", "content");
        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("1.5.3")))
                .willReturn(false);
        given(postValidator.isPostPublished(TEST_COMM_POST_ULID)).willReturn(true);
        // sibling "1.5.2" does not exist
        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("1.5.2")))
                .willReturn(false);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_EXIST_SIBLING_COMMENT);
    }

    @Test
    @DisplayName("중첩 경로 마지막 숫자가 1 초과일 때 - 형제 댓글이 있으면 등록 성공")
    void testRegister_givenNestedPathWithExistingSiblingComment_willSaveComment() {
        // given: path "1.5.3" requires sibling "1.5.2" to exist
        String content = "nested reply";
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_COMM_POST_ULID, "1.5.3", content);
        Comment mockComment = Mockito.mock(Comment.class);

        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("1.5.3")))
                .willReturn(false);
        given(postValidator.isPostPublished(TEST_COMM_POST_ULID)).willReturn(true);
        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("1.5.2")))
                .willReturn(true);
        given(swearService.filterSwear(content)).willReturn(content);
        given(mapper.toComment(any(), any(), any(), any())).willReturn(mockComment);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(writeRepository).should(times(1)).save(mockComment);
    }

    @Test
    @DisplayName("비속어 필터링이 적용된 내용으로 댓글이 저장됨")
    void testRegister_givenContentWithSwear_willSaveCommentWithFilteredContent() {
        // given
        String rawContent = "욕설 포함 내용";
        String filteredContent = "*** 포함 내용";
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_COMM_POST_ULID, "1", rawContent);
        Comment mockComment = Mockito.mock(Comment.class);

        given(readRepository.existsByPostAndPath(any(), any())).willReturn(false);
        given(postValidator.isPostPublished(TEST_COMM_POST_ULID)).willReturn(true);
        given(readRepository.countPostComment(PostId.create(TEST_COMM_POST_ULID))).willReturn(0);
        given(swearService.filterSwear(rawContent)).willReturn(filteredContent);
        given(mapper.toComment(any(), any(), any(),
                eq(CommentContent.create(filteredContent)))).willReturn(mockComment);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(swearService).should(times(1)).filterSwear(rawContent);
        then(mapper).should(times(1))
                .toComment(any(), any(), any(), eq(CommentContent.create(filteredContent)));
    }

    @Test
    @DisplayName("존재하지 않는 댓글 경로로 수정 시 NotFoundEntityException 발생")
    void testUpdate_givenNonExistentComment_willThrowNotFoundEntityException() {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest(TEST_COMM_POST_ULID, "1", "updated content");
        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("1")))
                .willReturn(false);

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> controller.update(request));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_COMMENT);
        assertThat(ex.getEntityName()).isEqualTo(TableName.COMM_COMMENT);
    }

    @Test
    @DisplayName("유효한 요청으로 댓글 수정 시 writeRepository.update 호출됨")
    void testUpdate_givenValidRequest_willCallRepositoryUpdate() {
        // given
        String updatedContent = "updated content";
        CommentUpdateRequest request = new CommentUpdateRequest(TEST_COMM_POST_ULID, "1", updatedContent);
        CommCommentId expectedId = CommCommentId.builder()
                .post(TEST_COMM_POST_ULID)
                .path("1")
                .build();

        given(readRepository.existsByPostAndPath(PostId.create(TEST_COMM_POST_ULID), CommentPath.create("1")))
                .willReturn(true);

        // when
        controller.update(request);

        // then
        then(writeRepository).should(times(1))
                .update(eq(expectedId), eq(CommentContent.create(updatedContent)));
    }

    @Test
    @DisplayName("유효한 요청으로 댓글 삭제 시 setCommentAsDeleted 호출됨")
    void testDelete_givenValidRequest_willCallSetCommentAsDeleted() {
        // given
        String commentPath = "1.2";
        CommCommentId expectedId = CommCommentId.builder()
                .post(TEST_COMM_POST_ULID)
                .path(commentPath)
                .build();

        // when
        controller.delete(TEST_COMM_POST_ULID, commentPath);

        // then
        then(writeRepository).should(times(1)).setCommentAsDeleted(eq(expectedId));
    }

    @Test
    @DisplayName("delete는 readRepository나 다른 의존성을 호출하지 않음")
    void testDelete_willOnlyInteractWithWriteRepository() {
        // when
        controller.delete(TEST_COMM_POST_ULID, "1");

        // then
        then(writeRepository).should(times(1)).setCommentAsDeleted(any());
        then(readRepository).shouldHaveNoInteractions();
        then(postJpaRepository).shouldHaveNoInteractions();
        then(memberJpaRepository).shouldHaveNoInteractions();
    }
}
