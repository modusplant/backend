package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.helper.CommentValidationHelper;
import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.event.CommentRegisterEvent;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.model.CommentOfAuthorReadModel;
import kr.modusplant.domains.comment.usecase.port.mapper.CommentMapper;
import kr.modusplant.domains.comment.usecase.port.repository.CommentCacheRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentCommandRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentQueryRepository;
import kr.modusplant.domains.comment.usecase.request.CommentDeleteRequest;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import kr.modusplant.domains.comment.usecase.request.CommentUpdateRequest;
import kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse;
import kr.modusplant.domains.comment.usecase.response.CommentPageResponse;
import kr.modusplant.infrastructure.swear.service.SwearService;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.NotAccessibleException;
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

public class CommentControllerTest implements PostIdTestUtils, AuthorTestUtils {
    private final CommentMapper mapper = Mockito.mock(CommentMapper.class);
    private final CommentQueryRepository queryRepository = Mockito.mock(CommentQueryRepository.class);
    private final CommentCommandRepository commandRepository = Mockito.mock(CommentCommandRepository.class);
    private final CommentCacheRepository cacheRepository = Mockito.mock(CommentCacheRepository.class);
    private final CommentValidationHelper validationHelper = Mockito.mock(CommentValidationHelper.class);
    private final SwearService swearService = Mockito.mock(SwearService.class);
    private final ApplicationEventPublisher publisher = Mockito.mock(ApplicationEventPublisher.class);
    private final CommentController controller = new CommentController(mapper, queryRepository,
            commandRepository, cacheRepository, validationHelper, swearService, publisher);

    // ---------- gatherByPost ----------

    @Test
    @DisplayName("존재하지 않는 게시글로 조회 시 예외 반환")
    void testGatherByPost_givenNonExistentPost_willThrowException() {
        // given
        doThrow(new NotFoundEntityException(EntityErrorCode.NOT_FOUND_POST, "post"))
                .when(validationHelper).validateIfPostExists(PostId.create(TEST_POST_ULID));

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> controller.gatherByPost(TEST_POST_ULID, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_POST);
        then(queryRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("유효한 게시글과 로그인 사용자로 조회 시 응답 반환")
    void testGatherByPost_givenAuthenticatedViewer_willReturnResponse() {
        // given
        CommentOfPostResponse response = Mockito.mock(CommentOfPostResponse.class);
        given(queryRepository.findByPost(PostId.create(TEST_POST_ULID),
                Author.createWithNullableUuid(MEMBER_BASIC_USER_UUID)))
                .willReturn(List.of(testCommentOfPostReadModel));
        given(mapper.toCommentOfPostResponse(testCommentOfPostReadModel)).willReturn(response);

        // when
        List<CommentOfPostResponse> result = controller.gatherByPost(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).containsExactly(response);
        then(validationHelper).should(times(1)).validateIfPostExists(PostId.create(TEST_POST_ULID));
    }

    @Test
    @DisplayName("유효한 게시글과 비로그인 사용자로 조회 시 응답 반환")
    void testGatherByPost_givenAnonymousViewer_willReturnResponse() {
        // given
        CommentOfPostResponse response = Mockito.mock(CommentOfPostResponse.class);
        given(queryRepository.findByPost(PostId.create(TEST_POST_ULID), Author.createWithNullableUuid(null)))
                .willReturn(List.of(testCommentOfPostReadModel));
        given(mapper.toCommentOfPostResponse(testCommentOfPostReadModel)).willReturn(response);

        // when
        List<CommentOfPostResponse> result = controller.gatherByPost(TEST_POST_ULID, null);

        // then
        assertThat(result).containsExactly(response);
    }

    @Test
    @DisplayName("댓글이 없는 게시글 조회 시 빈 응답 반환")
    void testGatherByPost_givenPostWithNoComments_willReturnResponse() {
        // given
        given(queryRepository.findByPost(any(PostId.class), any(Author.class)))
                .willReturn(Collections.emptyList());

        // when
        List<CommentOfPostResponse> result = controller.gatherByPost(TEST_POST_ULID, MEMBER_BASIC_USER_UUID);

        // then
        assertThat(result).isEmpty();
        then(mapper).shouldHaveNoInteractions();
    }

    // ---------- gatherByAuthor ----------

    @Test
    @DisplayName("존재하지 않는 작성자로 조회 시 예외 반환")
    void testGatherByAuthor_givenNonExistentAuthor_willThrowException() {
        // given
        doThrow(new NotFoundEntityException(CommentErrorCode.NOT_EXIST_AUTHOR, "author"))
                .when(validationHelper).validateIfAuthorExists(Author.create(MEMBER_BASIC_USER_UUID));

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> controller.gatherByAuthor(MEMBER_BASIC_USER_UUID, Pageable.unpaged()));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_EXIST_AUTHOR);
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
        CommentPageResponse<CommentOfAuthorReadModel> response = Mockito.mock(CommentPageResponse.class);

        given(queryRepository.findByAuthor(Author.create(MEMBER_BASIC_USER_UUID), pageable)).willReturn(page);
        given(mapper.toCommentPageResponseWithOnePlusPage(page)).willReturn(response);

        // when
        CommentPageResponse<CommentOfAuthorReadModel> result =
                controller.gatherByAuthor(MEMBER_BASIC_USER_UUID, pageable);

        // then
        assertThat(result).isEqualTo(response);
        then(validationHelper).should(times(1)).validateIfAuthorExists(Author.create(MEMBER_BASIC_USER_UUID));
    }

    // ---------- register ----------

    @Test
    @DisplayName("부모 댓글이 없는 중첩 경로로 등록 시 예외 반환")
    void testRegister_givenNestedPathWithoutParentComment_willThrowException() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_POST_ULID, "1.2", "content");
        given(queryRepository.isCommentExists(PostId.create(TEST_POST_ULID), CommentPath.create("1")))
                .willReturn(false);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> controller.register(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_EXIST_PARENT_COMMENT);
        then(cacheRepository).shouldHaveNoInteractions();
        then(commandRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("이미 처리된 등록 요청(경로 예약 없음)은 저장/이벤트 없이 활동 수행")
    void testRegister_givenAlreadyReservedRequest_willProcessAction() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_POST_ULID, "1", "content");
        given(cacheRepository.reservePath(eq(PostId.create(TEST_POST_ULID)), eq(CommentPath.create("1")),
                eq(Author.create(MEMBER_BASIC_USER_UUID)), any())).willReturn(Optional.empty());

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
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_POST_ULID, "1", content);
        given(cacheRepository.reservePath(eq(PostId.create(TEST_POST_ULID)), eq(CommentPath.create("1")),
                eq(Author.create(MEMBER_BASIC_USER_UUID)), any()))
                .willReturn(Optional.of(CommentPath.create("2")));
        given(swearService.filterSwear(content)).willReturn(content);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        then(commandRepository).should(times(1)).save(captor.capture());
        assertThat(captor.getValue().getPostId()).isEqualTo(PostId.create(TEST_POST_ULID));
        assertThat(captor.getValue().getPath()).isEqualTo(CommentPath.create("2"));
        assertThat(captor.getValue().getContent()).isEqualTo(CommentContent.create(content));
        then(publisher).should(times(1)).publishEvent(any(CommentRegisterEvent.class));
        then(queryRepository).should(never()).isCommentExists(any(), any());
    }

    @Test
    @DisplayName("부모 댓글이 있는 중첩 경로 등록 시 저장하는 활동 수행")
    void testRegister_givenNestedPathWithExistingParentComment_willProcessAction() {
        // given
        String content = "reply";
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_POST_ULID, "1.2", content);
        given(queryRepository.isCommentExists(PostId.create(TEST_POST_ULID), CommentPath.create("1")))
                .willReturn(true);
        given(cacheRepository.reservePath(eq(PostId.create(TEST_POST_ULID)), eq(CommentPath.create("1.2")),
                eq(Author.create(MEMBER_BASIC_USER_UUID)), any()))
                .willReturn(Optional.of(CommentPath.create("1.2")));
        given(swearService.filterSwear(content)).willReturn(content);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(commandRepository).should(times(1)).save(any(Comment.class));
        then(publisher).should(times(1)).publishEvent(any(CommentRegisterEvent.class));
    }

    @Test
    @DisplayName("비속어가 포함된 내용은 필터링된 내용으로 저장하는 활동 수행")
    void testRegister_givenContentContainingProfanity_willProcessAction() {
        // given
        String rawContent = "욕설 포함 내용";
        String filteredContent = "*** 포함 내용";
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_POST_ULID, "1", rawContent);
        given(cacheRepository.reservePath(any(), any(), any(), any()))
                .willReturn(Optional.of(CommentPath.create("2")));
        given(swearService.filterSwear(rawContent)).willReturn(filteredContent);

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(swearService).should(times(1)).filterSwear(rawContent);
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        then(commandRepository).should(times(1)).save(captor.capture());
        assertThat(captor.getValue().getContent()).isEqualTo(CommentContent.create(filteredContent));
    }

    @Test
    @DisplayName("유효한 등록 요청은 작성 가능 여부 검증을 헬퍼에 위임하는 활동 수행")
    void testRegister_givenValidRequest_willProcessAction() {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(TEST_POST_ULID, "1", "content");
        given(cacheRepository.reservePath(any(), any(), any(), any()))
                .willReturn(Optional.of(CommentPath.create("2")));
        given(swearService.filterSwear("content")).willReturn("content");

        // when
        controller.register(request, MEMBER_BASIC_USER_UUID);

        // then
        then(validationHelper).should(times(1)).validateIfAuthorCanWriteWithinPost(
                PostId.create(TEST_POST_ULID), Author.create(MEMBER_BASIC_USER_UUID));
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
                PostId.create(TEST_POST_ULID), CommentPath.create("1.2"), Author.create(MEMBER_BASIC_USER_UUID));
        then(commandRepository).should(times(1)).updateContent(
                PostId.create(TEST_POST_ULID), CommentPath.create("1.2"), CommentContent.create(updatedContent));
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
    @DisplayName("유효한 삭제 요청 시 검증 위임 후 삭제 처리하는 활동 수행")
    void testDelete_givenValidRequest_willProcessAction() {
        // given
        CommentDeleteRequest request = new CommentDeleteRequest(TEST_POST_ULID, "1.2");

        // when
        controller.delete(request, MEMBER_BASIC_USER_UUID);

        // then
        then(validationHelper).should(times(1)).validateIfAuthorCanWriteCommentWithinPost(
                PostId.create(TEST_POST_ULID), CommentPath.create("1.2"), Author.create(MEMBER_BASIC_USER_UUID));
        then(commandRepository).should(times(1)).setCommentAsDeleted(
                PostId.create(TEST_POST_ULID), CommentPath.create("1.2"));
    }

    @Test
    @DisplayName("작성자 본인이 아닌 사용자가 삭제 요청 시 예외 반환")
    void testDelete_givenAuthorIsNotWriter_willThrowException() {
        // given
        CommentDeleteRequest request = new CommentDeleteRequest(TEST_POST_ULID, "1.2");
        doThrow(new NotAccessibleException(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR, "comment", "1.2"))
                .when(validationHelper).validateIfAuthorCanWriteCommentWithinPost(any(), any(), any());

        // when
        NotAccessibleException ex = assertThrows(NotAccessibleException.class,
                () -> controller.delete(request, MEMBER_BASIC_USER_UUID));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR);
        then(commandRepository).should(never()).setCommentAsDeleted(any(), any());
    }
}
