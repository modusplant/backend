package kr.modusplant.domains.post.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.post.common.util.domain.vo.AuthorIdTestUtils;
import kr.modusplant.domains.post.domain.exception.AuthorNotFoundException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.shared.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_ROLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class PostAuthorRepositoryJpaAdapterTest implements AuthorIdTestUtils, MemberEntityTestUtils {
    private final MemberJpaRepository authorJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final PostAuthorRepositoryJpaAdapter postAuthorRepositoryJpaAdapter =
            new PostAuthorRepositoryJpaAdapter(authorJpaRepository);

    @Test
    @DisplayName("존재하는 작성자의 역할 조회 시 Role 반환")
    void testGetRole_givenExistingAuthor_willReturnRole() {
        // given
        given(authorJpaRepository.findByUuid(testPostAuthorId.getValue()))
                .willReturn(Optional.of(createMemberBasicUserEntityWithUuid()));

        // when
        Role result = postAuthorRepositoryJpaAdapter.getRole(testPostAuthorId);

        // then
        assertThat(result).isEqualTo(MEMBER_BASIC_USER_ROLE);
        verify(authorJpaRepository).findByUuid(testPostAuthorId.getValue());
    }

    @Test
    @DisplayName("존재하지 않는 작성자의 역할 조회 시 예외 반환")
    void testGetRole_givenNonExistentAuthor_willThrowException() {
        // given
        given(authorJpaRepository.findByUuid(testPostAuthorId.getValue())).willReturn(Optional.empty());

        // when
        AuthorNotFoundException exception = assertThrows(AuthorNotFoundException.class,
                () -> postAuthorRepositoryJpaAdapter.getRole(testPostAuthorId));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PostErrorCode.AUTHOR_NOT_FOUND);
    }
}
