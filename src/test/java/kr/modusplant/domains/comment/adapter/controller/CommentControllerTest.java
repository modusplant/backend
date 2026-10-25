package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.helper.CommentValidationHelper;
import kr.modusplant.domains.comment.common.util.domain.vo.CommentAuthorIdTestUtils;
import kr.modusplant.domains.comment.common.util.domain.vo.ImmediateParentPostIdTestUtils;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.event.CommentRegisterEvent;
import kr.modusplant.domains.comment.domain.exception.DeletedReplyToCommentException;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.*;
import kr.modusplant.domains.comment.domain.vo.nullobject.EmptyReplyToCommentPath;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.model.CommentOfPostReadModel;
import kr.modusplant.domains.comment.usecase.port.mapper.CommentMapper;
import kr.modusplant.domains.comment.usecase.port.repository.CommentCacheRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentCommandRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentQueryRepository;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.usecase.request.CommentUpdateRequest;
import kr.modusplant.domains.comment.usecase.response.CommentCursorPageResponse;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentOffsetPageResponse;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.framework.jdbc.holder.SwearHolder;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.comment.common.util.usecase.model.CommentOfAuthorPageModelTestUtils.testCommentOfAuthorReadModel;
import static kr.modusplant.domains.comment.common.util.usecase.model.CommentOfPostReadModelTestUtils.testCommentOfPostReadModel;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

public class CommentControllerTest implements ImmediateParentPostIdTestUtils, CommentAuthorIdTestUtils {
    private final CommentMapper mapper = Mockito.mock(CommentMapper.class);
    private final CommentQueryRepository queryRepository = Mockito.mock(CommentQueryRepository.class);
    private final CommentCommandRepository commandRepository = Mockito.mock(CommentCommandRepository.class);
    private final CommentCacheRepository cacheRepository = Mockito.mock(CommentCacheRepository.class);
    private final CommentValidationHelper validationHelper = Mockito.mock(CommentValidationHelper.class);
    private final SwearHolder swearHolder = Mockito.mock(SwearHolder.class);
    private final ApplicationEventPublisher publisher = Mockito.mock(ApplicationEventPublisher.class);
    private final CommentController controller = new CommentController(mapper, queryRepository,
            commandRepository, cacheRepository, validationHelper, swearHolder, publisher);

    // ---------- gatherByPost ----------

    @Test
    @DisplayName("존재하지 않는 게시글로 조회 시 예외 반환")
    void testGatherByPost_V1_givenNonExistentPost_willThrowException() {
        // given
        doThrow(new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, "post"))
                .when(validationHelper).validateIfImmediateParentPostExists(ImmediateParentPostId.create(TEST_POST_ULID));

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> controller.gatherByPost_V1(TEST_POST_ULID, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_POST);
        then(queryRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("유효한 게시글과 로그인 사용자로 조회 시 응답 반환")
    void testGatherByPost_V1_givenAuthenticatedViewer_willReturnResponse() {
        // given
        CommentOfPostResponse response = Mockito.mock(CommentOfPostResponse.class);
        given(queryRepository.findByPost(ImmediateParentPostId.create(TEST_POST_ULID),
                CommentAuthorId.fromNullableUuid(MEMBER_BASIC_USER_UUID)))
                .willReturn(List.of(testCommentOfPostReadModel));
        given(mapper.toCommentOfPostResponse(testCommentOfPostReadModel)).willReturn(response);

        // when
        List<CommentOfPostResponse> result = controller.gatherByPost_V1(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).containsExactly(response);
        then(validationHelper).should(times(1)).validateIfImmediateParentPostExists(ImmediateParentPostId.create(TEST_POST_ULID));
    }

    @Test
    @DisplayName("유효한 게시글과 비로그인 사용자로 조회 시 응답 반환")
    void testGatherByPost_V1_givenAnonymousViewer_willReturnResponse() {
        // given
        CommentOfPostResponse response = Mockito.mock(CommentOfPostResponse.class);
        given(queryRepository.findByPost(ImmediateParentPostId.create(TEST_POST_ULID), CommentAuthorId.fromNullableUuid(null)))
                .willReturn(List.of(testCommentOfPostReadModel));
        given(mapper.toCommentOfPostResponse(testCommentOfPostReadModel)).willReturn(response);

        // when
        List<CommentOfPostResponse> result = controller.gatherByPost_V1(TEST_POST_ULID, null);

        // then
        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("댓글이 없는 게시글 조회 시 빈 응답 반환")
    void testGatherByPost_V1_givenPostWithNoComments_willReturnResponse() {
        // given
        given(queryRepository.findByPost(any(ImmediateParentPostId.class), any(CommentAuthorId.class)))
                .willReturn(Collections.emptyList());

        // when
        List<CommentOfPostResponse> result = controller.gatherByPost_V1(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).isEmpty();
        then(mapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("범위를 벗어난 페이지 크기로 조회 시 예외 반환")
    void testGatherByPost_V2_givenOutOfRangeSize_willThrowException() {
        // given & when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.gatherByPost_V2(TEST_POST_ULID, null, 51, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.COMMENT_PAGE_SIZE_OUT_OF_RANGE);
        then(validationHelper).shouldHaveNoInteractions();
        then(queryRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 게시글로 커서 조회 시 예외 반환")
    void testGatherByPost_V2_givenNonExistentPost_willThrowException() {
        // given
        doThrow(new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, "post"))
                .when(validationHelper).validateIfImmediateParentPostExists(ImmediateParentPostId.create(TEST_POST_ULID));

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> controller.gatherByPost_V2(TEST_POST_ULID, null, 2, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_POST);
        then(queryRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 마지막 댓글 경로로 커서 조회 시 예외 반환")
    void testGatherByPost_V2_givenNonExistentLastPath_willThrowException() {
        // given
        doThrow(new NotFoundEntityException(EntityErrorCode.NOT_FOUND_COMMENT, "comm_comment"))
                .when(validationHelper).validateIfCommentExists(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("3"));

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> controller.gatherByPost_V2(TEST_POST_ULID, "3", 2, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_COMMENT);
        then(queryRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("페이지 크기보다 많은 댓글이 조회된 첫 페이지 조회 시 CommentCursorPageResponse 반환")
    void testGatherByPost_V2_givenFirstPageWithNextPage_willReturnCommentCursorPageResponse() {
        // given
        CommentOfPostReadModel firstReadModel = Mockito.mock(CommentOfPostReadModel.class);
        CommentOfPostReadModel secondReadModel = Mockito.mock(CommentOfPostReadModel.class);
        CommentOfPostReadModel thirdReadModel = Mockito.mock(CommentOfPostReadModel.class);
        CommentOfPostResponse firstResponse = Mockito.mock(CommentOfPostResponse.class);
        CommentOfPostResponse secondResponse = Mockito.mock(CommentOfPostResponse.class);
        CommentOfPostResponse thirdResponse = Mockito.mock(CommentOfPostResponse.class);
        given(queryRepository.findByPost(ImmediateParentPostId.create(TEST_POST_ULID), null, CommentPageSize.create(2),
                CommentAuthorId.fromNullableUuid(MEMBER_BASIC_USER_UUID)))
                .willReturn(List.of(firstReadModel, secondReadModel, thirdReadModel));
        given(mapper.toCommentOfPostResponse(firstReadModel)).willReturn(firstResponse);
        given(mapper.toCommentOfPostResponse(secondReadModel)).willReturn(secondResponse);
        given(mapper.toCommentOfPostResponse(thirdReadModel)).willReturn(thirdResponse);
        given(secondResponse.path()).willReturn("1.1");

        // when
        CommentCursorPageResponse result =
                controller.gatherByPost_V2(TEST_POST_ULID, null, 2, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result.commentList()).containsExactly(firstResponse, secondResponse);
        assertThat(result.lastPath()).isEqualTo("1.1");
        assertThat(result.hasNext()).isTrue();
        assertThat(result.size()).isEqualTo(2);
        then(validationHelper).should(times(1)).validateIfImmediateParentPostExists(ImmediateParentPostId.create(TEST_POST_ULID));
        then(validationHelper).should(never()).validateIfCommentExists(any(), any());
    }

    @Test
    @DisplayName("마지막 댓글 경로 이후의 마지막 페이지 조회 시 CommentCursorPageResponse 반환")
    void testGatherByPost_V2_givenLastPathWithLastPage_willReturnCommentCursorPageResponse() {
        // given
        CommentOfPostReadModel readModel = Mockito.mock(CommentOfPostReadModel.class);
        CommentOfPostResponse response = Mockito.mock(CommentOfPostResponse.class);
        given(queryRepository.findByPost(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("3"),
                CommentPageSize.create(2), CommentAuthorId.fromNullableUuid(null)))
                .willReturn(List.of(readModel));
        given(mapper.toCommentOfPostResponse(readModel)).willReturn(response);
        given(response.path()).willReturn("4");

        // when
        CommentCursorPageResponse result = controller.gatherByPost_V2(TEST_POST_ULID, "3", 2, null);

        // then
        assertThat(result.commentList()).containsExactly(response);
        assertThat(result.lastPath()).isEqualTo("4");
        assertThat(result.hasNext()).isFalse();
        assertThat(result.size()).isEqualTo(1);
        then(validationHelper).should(times(1))
                .validateIfCommentExists(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("3"));
    }

    @Test
    @DisplayName("댓글이 없는 게시글 커서 조회 시 CommentCursorPageResponse 반환")
    void testGatherByPost_V2_givenPostWithNoComments_willReturnCommentCursorPageResponse() {
        // given
        given(queryRepository.findByPost(any(ImmediateParentPostId.class), any(), any(CommentPageSize.class), any(CommentAuthorId.class)))
                .willReturn(Collections.emptyList());

        // when
        CommentCursorPageResponse result =
                controller.gatherByPost_V2(TEST_POST_ULID, null, 2, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result.commentList()).isEmpty();
        assertThat(result.lastPath()).isNull();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.size()).isZero();
        then(mapper).shouldHaveNoInteractions();
    }

    // ---------- gatherByAuthor ----------

    @Test
    @DisplayName("존재하지 않는 작성자로 조회 시 예외 반환")
    void testGatherByAuthor_givenNonExistentAuthor_willThrowException() {
        // given
        doThrow(new NotFoundEntityException(CommentErrorCode.NOT_FOUND_COMMENT_AUTHOR, "commentAuthor"))
                .when(validationHelper).validateIfAuthorExists(CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID));

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> controller.gatherByAuthor(MEMBER_BASIC_USER_UUID, Pageable.unpaged()));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_FOUND_COMMENT_AUTHOR);
        then(queryRepository).shouldHaveNoInteractions();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("유효한 작성자로 조회 시 응답 반환")
    void testGatherByAuthor_givenValidAuthor_willReturnResponse() {
        // given
        Pageable pageable = PageRequest.of(0, 1);
        PageImpl<CommentOfAuthorReadModel> page =
                new PageImpl<>(List.of(testCommentOfAuthorReadModel), pageable, 1L);
        CommentOffsetPageResponse<CommentOfAuthorReadModel> response = Mockito.mock(CommentOffsetPageResponse.class);

        given(queryRepository.findByAuthor(CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID), pageable)).willReturn(page);
        given(mapper.toCommentPageResponseWithOnePlusPage(page)).willReturn(response);

        // when
        CommentOffsetPageResponse<CommentOfAuthorReadModel> result =
                controller.gatherByAuthor(MEMBER_BASIC_USER_UUID, pageable);

        // then
        assertThat(result).isEqualTo(response);
        then(validationHelper).should(times(1)).validateIfAuthorExists(CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID));
    }

    // ---------- register ----------

    @Test
    @DisplayName("부모 댓글이 존재하지 않는 대댓글 경로로 등록 시 예외 반환")
    void testRegister_givenNonExistentParentComment_willThrowException() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(null, TEST_POST_ULID, "1.2", "content");
        given(queryRepository.isCommentExists(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1"))).willReturn(false);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_FOUND_PARENT_COMMENT);
        then(cacheRepository).shouldHaveNoInteractions();
        then(commandRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("깊이가 2가 아닌 답글 대상 경로로 등록 시 예외 반환")
    void testRegister_givenRootReplyToPath_willThrowException() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest("1", TEST_POST_ULID, "1.2", "content");
        given(queryRepository.isCommentExists(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1"))).willReturn(true);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.INVALID_REPLY_TO_COMMENT_PATH);
        then(validationHelper).should(never()).validateIfCommentExists(any(), any());
        then(cacheRepository).shouldHaveNoInteractions();
        then(commandRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("최상위 댓글 경로에 답글 대상 경로를 지정해 등록 시 예외 반환")
    void testRegister_givenRootPathWithReplyToPath_willThrowException() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest("1.1", TEST_POST_ULID, "1", "content");

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.INVALID_REPLY_TO_COMMENT_PATH);
        then(cacheRepository).shouldHaveNoInteractions();
        then(commandRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("다른 최상위 댓글에 속한 답글 대상 경로로 등록 시 예외 반환")
    void testRegister_givenReplyToPathUnderDifferentRoot_willThrowException() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest("2.1", TEST_POST_ULID, "1.2", "content");
        given(queryRepository.isCommentExists(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1"))).willReturn(true);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.INVALID_REPLY_TO_COMMENT_PATH);
        then(cacheRepository).shouldHaveNoInteractions();
        then(commandRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 답글 대상 댓글로 등록 시 예외 반환")
    void testRegister_givenNonExistentReplyToComment_willThrowException() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest("1.1", TEST_POST_ULID, "1.2", "content");
        given(queryRepository.isCommentExists(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1"))).willReturn(true);
        doThrow(new NotFoundEntityException(EntityErrorCode.NOT_FOUND_COMMENT, "comm_comment"))
                .when(validationHelper).validateIfCommentExists(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1.1"));

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_COMMENT);
        then(cacheRepository).shouldHaveNoInteractions();
        then(commandRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("삭제된 답글 대상 댓글로 등록 시 예외 반환")
    void testRegister_givenDeletedReplyToComment_willThrowException() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest("1.1", TEST_POST_ULID, "1.2", "content");
        given(queryRepository.isCommentExists(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1"))).willReturn(true);
        doThrow(new DeletedReplyToCommentException())
                .when(validationHelper).validateIfCommentIsNotDeleted(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1.1"));

        // when
        DeletedReplyToCommentException ex = assertThrows(DeletedReplyToCommentException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.DELETED_REPLY_TO_COMMENT);
        then(cacheRepository).shouldHaveNoInteractions();
        then(commandRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("유효한 답글 대상 경로로 등록 시 답글 대상 경로와 함께 저장하는 활동 수행")
    void testRegister_givenValidReplyToPath_willProcessAction() {
        // given
        String content = "reply";
        CommentRegisterRequest request = new CommentRegisterRequest("1.1", TEST_POST_ULID, "1.2", content);
        given(queryRepository.isCommentExists(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1"))).willReturn(true);
        given(cacheRepository.reservePath(eq(ImmediateParentPostId.create(TEST_POST_ULID)), eq(CommentPath.create("1.2")),
                eq(CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID)), any()))
                .willReturn(Optional.of(CommentPath.create("1.3")));
        given(swearHolder.filterSwear(content)).willReturn(content);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(validationHelper).should(times(1))
                .validateIfCommentExists(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1.1"));
        then(validationHelper).should(times(1))
                .validateIfCommentIsNotDeleted(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1.1"));
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        then(commandRepository).should(times(1)).save(captor.capture());
        assertThat(captor.getValue().getPath()).isEqualTo(CommentPath.create("1.3"));
        assertThat(captor.getValue().getReplyToPath()).isEqualTo(ReplyToCommentPath.create("1.1"));
        then(publisher).should(times(1)).publishEvent(any(CommentRegisterEvent.class));
    }

    @Test
    @DisplayName("이미 처리된 등록 요청(경로 예약 없음)은 저장/이벤트 없이 활동 수행")
    void testRegister_givenAlreadyReservedRequest_willProcessAction() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(null, TEST_POST_ULID, "1", "content");
        given(cacheRepository.reservePath(eq(ImmediateParentPostId.create(TEST_POST_ULID)), eq(CommentPath.create("1")),
                eq(CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID)), any())).willReturn(Optional.empty());

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(commandRepository).should(never()).save(any());
        then(publisher).should(never()).publishEvent(any());
    }

    @Test
    @DisplayName("새 최상위 댓글 등록 시 예약된 경로로 저장하고 이벤트 발행하는 활동 수행")
    void testRegister_givenNewRootComment_willProcessAction() {
        // given
        String content = "hello";
        CommentRegisterRequest request = new CommentRegisterRequest(null, TEST_POST_ULID, "1", content);
        given(cacheRepository.reservePath(eq(ImmediateParentPostId.create(TEST_POST_ULID)), eq(CommentPath.create("1")),
                eq(CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID)), any()))
                .willReturn(Optional.of(CommentPath.create("2")));
        given(swearHolder.filterSwear(content)).willReturn(content);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        then(commandRepository).should(times(1)).save(captor.capture());
        assertThat(captor.getValue().getImmediateParentPostId()).isEqualTo(ImmediateParentPostId.create(TEST_POST_ULID));
        assertThat(captor.getValue().getPath()).isEqualTo(CommentPath.create("2"));
        assertThat(captor.getValue().getContent()).isEqualTo(CommentContent.create(content));
        assertThat(captor.getValue().getReplyToPath()).isSameAs(EmptyReplyToCommentPath.create());
        then(publisher).should(times(1)).publishEvent(any(CommentRegisterEvent.class));
        then(validationHelper).should(never()).validateIfCommentExists(any(), any());
    }

    @Test
    @DisplayName("답글 대상 경로가 없는 대댓글 등록 시 저장하는 활동 수행")
    void testRegister_givenNestedPathWithoutReplyToPath_willProcessAction() {
        // given
        String content = "reply";
        CommentRegisterRequest request = new CommentRegisterRequest(null, TEST_POST_ULID, "1.2", content);
        given(queryRepository.isCommentExists(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1"))).willReturn(true);
        given(cacheRepository.reservePath(eq(ImmediateParentPostId.create(TEST_POST_ULID)), eq(CommentPath.create("1.2")),
                eq(CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID)), any()))
                .willReturn(Optional.of(CommentPath.create("1.2")));
        given(swearHolder.filterSwear(content)).willReturn(content);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(validationHelper).should(never()).validateIfCommentExists(any(), any());
        then(commandRepository).should(times(1)).save(any(Comment.class));
        then(publisher).should(times(1)).publishEvent(any(CommentRegisterEvent.class));
    }

    @Test
    @DisplayName("빈 문자열 답글 대상 경로로 대댓글 등록 시 답글 대상 경로 없이 저장하는 활동 수행")
    void testRegister_givenBlankReplyToPath_willProcessAction() {
        // given
        String content = "reply";
        CommentRegisterRequest request = new CommentRegisterRequest("", TEST_POST_ULID, "1.2", content);
        given(queryRepository.isCommentExists(ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1"))).willReturn(true);
        given(cacheRepository.reservePath(eq(ImmediateParentPostId.create(TEST_POST_ULID)), eq(CommentPath.create("1.2")),
                eq(CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID)), any()))
                .willReturn(Optional.of(CommentPath.create("1.2")));
        given(swearHolder.filterSwear(content)).willReturn(content);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(validationHelper).should(never()).validateIfCommentExists(any(), any());
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        then(commandRepository).should(times(1)).save(captor.capture());
        assertThat(captor.getValue().getReplyToPath()).isSameAs(EmptyReplyToCommentPath.create());
    }

    @Test
    @DisplayName("최대 깊이를 초과하는 답글 대상 경로로 등록 시 예외 반환")
    void testRegister_givenReplyToPathDeeperThanMaxDepth_willThrowException() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest("1.1.1", TEST_POST_ULID, "1.2", "content");

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.INVALID_COMMENT_PATH_DEPTH);
        then(validationHelper).shouldHaveNoInteractions();
        then(cacheRepository).shouldHaveNoInteractions();
        then(commandRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("비속어가 포함된 내용은 필터링된 내용으로 저장하는 활동 수행")
    void testRegister_givenContentContainingProfanity_willProcessAction() {
        // given
        String rawContent = "욕설 포함 내용";
        String filteredContent = "*** 포함 내용";
        CommentRegisterRequest request = new CommentRegisterRequest(null, TEST_POST_ULID, "1", rawContent);
        given(cacheRepository.reservePath(any(), any(), any(), any()))
                .willReturn(Optional.of(CommentPath.create("2")));
        given(swearHolder.filterSwear(rawContent)).willReturn(filteredContent);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(swearHolder).should(times(1)).filterSwear(rawContent);
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        then(commandRepository).should(times(1)).save(captor.capture());
        assertThat(captor.getValue().getContent()).isEqualTo(CommentContent.create(filteredContent));
    }

    @Test
    @DisplayName("유효한 등록 요청은 작성 가능 여부 검증을 헬퍼에 위임하는 활동 수행")
    void testRegister_givenValidRequest_willProcessAction() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(null, TEST_POST_ULID, "1", "content");
        given(cacheRepository.reservePath(any(), any(), any(), any()))
                .willReturn(Optional.of(CommentPath.create("2")));
        given(swearHolder.filterSwear("content")).willReturn("content");

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(validationHelper).should(times(1)).validateIfAuthorCanWriteWithinPost(
                ImmediateParentPostId.create(TEST_POST_ULID), CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID));
    }

    // ---------- updateContent ----------

    @Test
    @DisplayName("유효한 수정 요청 시 검증 위임 후 내용 갱신하는 활동 수행")
    void testUpdateContent_givenValidRequest_willProcessAction() {
        // given
        String updatedContent = "updated content";
        CommentUpdateRequest request = new CommentUpdateRequest(TEST_POST_ULID, "1.2", updatedContent);

        // when
        controller.updateContent(request, MEMBER_BASIC_USER_UUID);

        // then
        then(validationHelper).should(times(1)).validateIfAuthorCanWriteCommentWithinPost(
                ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1.2"), CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID));
        then(commandRepository).should(times(1)).updateContent(
                ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create("1.2"), CommentContent.create(updatedContent));
    }

    @Test
    @DisplayName("작성자 본인이 아닌 사용자가 수정 요청 시 예외 반환")
    void testUpdateContent_givenAuthorIsNotWriter_willThrowException() {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest(TEST_POST_ULID, "1.2", "updated content");
        doThrow(new NotAccessibleException(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR, "comment", "1.2"))
                .when(validationHelper).validateIfAuthorCanWriteCommentWithinPost(any(), any(), any());

        // when
        NotAccessibleException ex = assertThrows(NotAccessibleException.class,
                () -> controller.updateContent(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR);
        then(commandRepository).should(never()).updateContent(any(), any(), any());
    }

    // ---------- delete ----------

    @Test
    @DisplayName("유효한 삭제 요청 시 검증 위임 후 댓글 삭제 처리하는 활동 수행")
    void testDelete_givenValidRequest_willProcessAction() {
        // given
        String commentPath = "1.2";

        // when
        controller.delete(TEST_POST_ULID, commentPath, MEMBER_BASIC_USER_UUID);

        // then
        then(validationHelper).should(times(1)).validateIfAuthorCanWriteCommentWithinPost(
                ImmediateParentPostId.create(TEST_POST_ULID), CommentPath.create(commentPath), CommentAuthorId.fromUuid(MEMBER_BASIC_USER_UUID));
        then(commandRepository).should(times(1))
                .deleteComment(eq(ImmediateParentPostId.create(TEST_POST_ULID)), eq(CommentPath.create(commentPath)));
    }

    @Test
    @DisplayName("작성자 본인이 아닌 사용자가 삭제 요청 시 예외 반환")
    void testDelete_givenAuthorIsNotWriter_willThrowException() {
        // given
        String commentPath = "1.2";
        doThrow(new NotAccessibleException(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR, "comment", commentPath))
                .when(validationHelper).validateIfAuthorCanWriteCommentWithinPost(any(), any(), any());

        // when
        NotAccessibleException ex = assertThrows(NotAccessibleException.class,
                () -> controller.delete(TEST_POST_ULID, commentPath, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR);
        then(commandRepository).should(never()).deleteComment(any(), any());
    }
}
