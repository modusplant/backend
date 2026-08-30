package kr.modusplant.domains.comment.adapter.helper;

import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentPathTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.framework.outbound.jpa.repository.CommentJpaRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentQueryRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.persistence.constant.TableName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

public class CommentValidationHelperTest implements PostIdTestUtils, CommentPathTestUtils, AuthorTestUtils {
    private final CommentQueryRepository queryRepository = Mockito.mock(CommentQueryRepository.class);
    private final CommentJpaRepository commentJpaRepository = Mockito.mock(CommentJpaRepository.class);
    private final PostJpaRepository postJpaRepository = Mockito.mock(PostJpaRepository.class);
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final CommentValidationHelper helper = new CommentValidationHelper(
            queryRepository, commentJpaRepository, postJpaRepository, memberJpaRepository);

    @Test
    @DisplayName("존재하는 작성자 검증 시 활동 수행")
    void testValidateIfAuthorExists_givenExistingAuthor_willProcessAction() {
        // given
        given(memberJpaRepository.existsById(testAuthorWithUuid.getUuid())).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfAuthorExists(testAuthorWithUuid));
        then(memberJpaRepository).should(times(1)).existsById(testAuthorWithUuid.getUuid());
    }

    @Test
    @DisplayName("존재하지 않는 작성자 검증 시 예외 반환")
    void testValidateIfAuthorExists_givenNonExistentAuthor_willThrowException() {
        // given
        given(memberJpaRepository.existsById(testAuthorWithUuid.getUuid())).willReturn(false);

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> helper.validateIfAuthorExists(testAuthorWithUuid));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_EXIST_AUTHOR);
        assertThat(ex.getEntityName()).isEqualTo("author");
    }

    @Test
    @DisplayName("존재하는 게시글 검증 시 활동 수행")
    void testValidateIfPostExists_givenExistingPost_willProcessAction() {
        // given
        given(postJpaRepository.existsByUlid(testPostId.getValue())).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfPostExists(testPostId));
        then(postJpaRepository).should(times(1)).existsByUlid(testPostId.getValue());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 검증 시 예외 반환")
    void testValidateIfPostExists_givenNonExistentPost_willThrowException() {
        // given
        given(postJpaRepository.existsByUlid(testPostId.getValue())).willReturn(false);

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> helper.validateIfPostExists(testPostId));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_POST);
        assertThat(ex.getEntityName()).isEqualTo("post");
    }

    @Test
    @DisplayName("게시된 게시글 검증 시 활동 수행")
    void testValidateIfPostIsPublished_givenPublishedPost_willProcessAction() {
        // given
        given(queryRepository.isPostPublished(testPostId)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfPostIsPublished(testPostId));
        then(queryRepository).should(times(1)).isPostPublished(testPostId);
    }

    @Test
    @DisplayName("게시되지 않은 게시글 검증 시 예외 반환")
    void testValidateIfPostIsPublished_givenUnpublishedPost_willThrowException() {
        // given
        given(queryRepository.isPostPublished(testPostId)).willReturn(false);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> helper.validateIfPostIsPublished(testPostId));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_PUBLISHED_POST);
    }

    @Test
    @DisplayName("존재하는 댓글 검증 시 활동 수행")
    void testValidateIfCommentExists_givenExistingComment_willProcessAction() {
        // given
        given(queryRepository.isCommentExists(testPostId, testCommentPath)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfCommentExists(testPostId, testCommentPath));
        then(queryRepository).should(times(1)).isCommentExists(testPostId, testCommentPath);
    }

    @Test
    @DisplayName("존재하지 않는 댓글 검증 시 예외 반환")
    void testValidateIfCommentExists_givenNonExistentComment_willThrowException() {
        // given
        given(queryRepository.isCommentExists(testPostId, testCommentPath)).willReturn(false);

        // when
        NotFoundEntityException ex = assertThrows(NotFoundEntityException.class,
                () -> helper.validateIfCommentExists(testPostId, testCommentPath));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(EntityErrorCode.NOT_FOUND_COMMENT);
        assertThat(ex.getEntityName()).isEqualTo(TableName.COMM_COMMENT);
    }

    @Test
    @DisplayName("작성자 본인이 작성한 댓글 검증 시 활동 수행")
    void testValidateIfCommentWrittenByAuthor_givenCommentWrittenByAuthor_willProcessAction() {
        // given
        given(commentJpaRepository.existsByPostUlidAndPathAndAuthMemberUuid(
                testPostId.getValue(), testCommentPath.getValue(), testAuthorWithUuid.getUuid())).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfCommentWrittenByAuthor(testPostId, testCommentPath, testAuthorWithUuid));
        then(commentJpaRepository).should(times(1)).existsByPostUlidAndPathAndAuthMemberUuid(
                testPostId.getValue(), testCommentPath.getValue(), testAuthorWithUuid.getUuid());
    }

    @Test
    @DisplayName("작성자 본인이 작성하지 않은 댓글 검증 시 예외 반환")
    void testValidateIfCommentWrittenByAuthor_givenCommentNotWrittenByAuthor_willThrowException() {
        // given
        given(commentJpaRepository.existsByPostUlidAndPathAndAuthMemberUuid(
                testPostId.getValue(), testCommentPath.getValue(), testAuthorWithUuid.getUuid())).willReturn(false);

        // when
        NotAccessibleException ex = assertThrows(NotAccessibleException.class,
                () -> helper.validateIfCommentWrittenByAuthor(testPostId, testCommentPath, testAuthorWithUuid));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR);
    }

    @Test
    @DisplayName("작성자가 게시글에 댓글을 쓸 수 있는 모든 조건 충족 시 활동 수행")
    void testValidateIfAuthorCanWriteWithinPost_givenAllConditionsMet_willProcessAction() {
        // given
        given(memberJpaRepository.existsById(testAuthorWithUuid.getUuid())).willReturn(true);
        given(postJpaRepository.existsByUlid(testPostId.getValue())).willReturn(true);
        given(queryRepository.isPostPublished(testPostId)).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfAuthorCanWriteWithinPost(testPostId, testAuthorWithUuid));
        then(memberJpaRepository).should(times(1)).existsById(testAuthorWithUuid.getUuid());
        then(postJpaRepository).should(times(1)).existsByUlid(testPostId.getValue());
        then(queryRepository).should(times(1)).isPostPublished(testPostId);
    }

    @Test
    @DisplayName("작성자가 게시글에 댓글을 쓸 때 게시글이 게시되지 않았으면 예외 반환")
    void testValidateIfAuthorCanWriteWithinPost_givenUnpublishedPost_willThrowException() {
        // given
        given(memberJpaRepository.existsById(testAuthorWithUuid.getUuid())).willReturn(true);
        given(postJpaRepository.existsByUlid(testPostId.getValue())).willReturn(true);
        given(queryRepository.isPostPublished(testPostId)).willReturn(false);

        // when
        InvalidValueException ex = assertThrows(InvalidValueException.class,
                () -> helper.validateIfAuthorCanWriteWithinPost(testPostId, testAuthorWithUuid));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_PUBLISHED_POST);
    }

    @Test
    @DisplayName("작성자가 기존 댓글을 수정/삭제할 수 있는 모든 조건 충족 시 활동 수행")
    void testValidateIfAuthorCanWriteCommentWithinPost_givenAllConditionsMet_willProcessAction() {
        // given
        given(memberJpaRepository.existsById(testAuthorWithUuid.getUuid())).willReturn(true);
        given(postJpaRepository.existsByUlid(testPostId.getValue())).willReturn(true);
        given(queryRepository.isPostPublished(testPostId)).willReturn(true);
        given(queryRepository.isCommentExists(testPostId, testCommentPath)).willReturn(true);
        given(commentJpaRepository.existsByPostUlidAndPathAndAuthMemberUuid(
                testPostId.getValue(), testCommentPath.getValue(), testAuthorWithUuid.getUuid())).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> helper.validateIfAuthorCanWriteCommentWithinPost(testPostId, testCommentPath, testAuthorWithUuid));
        then(queryRepository).should(times(1)).isCommentExists(testPostId, testCommentPath);
        then(commentJpaRepository).should(times(1)).existsByPostUlidAndPathAndAuthMemberUuid(
                testPostId.getValue(), testCommentPath.getValue(), testAuthorWithUuid.getUuid());
    }

    @Test
    @DisplayName("작성자가 기존 댓글을 수정/삭제할 때 본인이 작성한 댓글이 아니면 예외 반환")
    void testValidateIfAuthorCanWriteCommentWithinPost_givenCommentNotWrittenByAuthor_willThrowException() {
        // given
        given(memberJpaRepository.existsById(testAuthorWithUuid.getUuid())).willReturn(true);
        given(postJpaRepository.existsByUlid(testPostId.getValue())).willReturn(true);
        given(queryRepository.isPostPublished(testPostId)).willReturn(true);
        given(queryRepository.isCommentExists(testPostId, testCommentPath)).willReturn(true);
        given(commentJpaRepository.existsByPostUlidAndPathAndAuthMemberUuid(
                testPostId.getValue(), testCommentPath.getValue(), testAuthorWithUuid.getUuid())).willReturn(false);

        // when
        NotAccessibleException ex = assertThrows(NotAccessibleException.class,
                () -> helper.validateIfAuthorCanWriteCommentWithinPost(testPostId, testCommentPath, testAuthorWithUuid));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(CommentErrorCode.NOT_WRITTEN_COMMENT_BY_AUTHOR);
    }
}
