package kr.modusplant.domains.comment.framework.outbound.jpa.repository;

import kr.modusplant.domains.comment.common.util.domain.vo.CommentAuthorIdTestUtils;
import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.exception.NotFoundValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_ROLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class CommentAuthorRepositoryJpaAdapterTest implements CommentAuthorIdTestUtils, MemberEntityTestUtils {
    private final MemberJpaRepository memberRepository = Mockito.mock(MemberJpaRepository.class);
    private final CommentAuthorRepositoryJpaAdapter commentAuthorRepositoryJpaAdapter =
            new CommentAuthorRepositoryJpaAdapter(memberRepository);

    @Test
    @DisplayName("존재하는 작성자의 역할 조회 시 Role 반환")
    void testGetRole_givenExistingAuthor_willReturnRole() {
        // given
        given(memberRepository.findByUuid(testCommentAuthorId.getValue()))
                .willReturn(Optional.of(createMemberBasicUserEntityWithUuid()));

        // when
        Role result = commentAuthorRepositoryJpaAdapter.getRole(testCommentAuthorId);

        // then
        assertThat(result).isEqualTo(MEMBER_BASIC_USER_ROLE);
        verify(memberRepository).findByUuid(testCommentAuthorId.getValue());
    }

    @Test
    @DisplayName("존재하지 않는 작성자의 역할 조회 시 예외 반환")
    void testGetRole_givenNonExistentAuthor_willThrowException() {
        // given
        given(memberRepository.findByUuid(testCommentAuthorId.getValue())).willReturn(Optional.empty());

        // when
        NotFoundValueException exception = assertThrows(NotFoundValueException.class,
                () -> commentAuthorRepositoryJpaAdapter.getRole(testCommentAuthorId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(CommentErrorCode.NOT_FOUND_COMMENT_AUTHOR);
        assertThat(exception.getMessage()).contains("[valueName: commentAuthorId]");
    }

    @Test
    @DisplayName("존재하는 작성자 여부 확인 시 true 반환")
    void testIsExists_givenExistingAuthor_willReturnTrue() {
        // given
        given(memberRepository.existsById(testCommentAuthorId.getValue())).willReturn(true);

        // when
        boolean result = commentAuthorRepositoryJpaAdapter.isExists(testCommentAuthorId);

        // then
        assertThat(result).isTrue();
        verify(memberRepository).existsById(testCommentAuthorId.getValue());
    }

    @Test
    @DisplayName("존재하지 않는 작성자 여부 확인 시 false 반환")
    void testIsExists_givenNonExistentAuthor_willReturnFalse() {
        // given
        given(memberRepository.existsById(testCommentAuthorId.getValue())).willReturn(false);

        // when
        boolean result = commentAuthorRepositoryJpaAdapter.isExists(testCommentAuthorId);

        // then
        assertThat(result).isFalse();
        verify(memberRepository).existsById(testCommentAuthorId.getValue());
    }
}
