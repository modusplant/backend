package kr.modusplant.domains.comment.framework.outbound.jpa.repository;

import kr.modusplant.domains.comment.common.util.domain.vo.ImmediateParentPostIdTestUtils;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PostEntityTestUtils;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class ImmediateParentPostRepositoryJpaAdapterTest implements ImmediateParentPostIdTestUtils, PostEntityTestUtils {
    private final PostJpaRepository postRepository = Mockito.mock(PostJpaRepository.class);
    private final ImmediateParentPostRepositoryJpaAdapter immediateParentPostRepositoryJpaAdapter =
            new ImmediateParentPostRepositoryJpaAdapter(postRepository);

    @Test
    @DisplayName("존재하는 게시글 여부 확인 시 true 반환")
    void testIsImmediateParentPostExists_givenExistingPost_willReturnTrue() {
        // given
        given(postRepository.existsByUlid(testImmediateParentPostId.getValue())).willReturn(true);

        // when
        boolean result = immediateParentPostRepositoryJpaAdapter.isImmediateParentPostExists(testImmediateParentPostId);

        // then
        assertThat(result).isTrue();
        verify(postRepository).existsByUlid(testImmediateParentPostId.getValue());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 여부 확인 시 false 반환")
    void testIsImmediateParentPostExists_givenNonExistentPost_willReturnFalse() {
        // given
        given(postRepository.existsByUlid(testImmediateParentPostId.getValue())).willReturn(false);

        // when
        boolean result = immediateParentPostRepositoryJpaAdapter.isImmediateParentPostExists(testImmediateParentPostId);

        // then
        assertThat(result).isFalse();
        verify(postRepository).existsByUlid(testImmediateParentPostId.getValue());
    }

    @Test
    @DisplayName("발행된 게시글 여부 확인 시 true 반환")
    void testIsImmediateParentPostPublished_givenPublishedPost_willReturnTrue() {
        // given
        given(postRepository.findByUlid(testImmediateParentPostId.getValue()))
                .willReturn(Optional.of(createPublishedPostEntityBuilderWithUuid().build()));

        // when
        boolean result = immediateParentPostRepositoryJpaAdapter.isImmediateParentPostPublished(testImmediateParentPostId);

        // then
        assertThat(result).isTrue();
        verify(postRepository).findByUlid(testImmediateParentPostId.getValue());
    }

    @Test
    @DisplayName("발행되지 않은 게시글 여부 확인 시 false 반환")
    void testIsImmediateParentPostPublished_givenDraftPost_willReturnFalse() {
        // given
        given(postRepository.findByUlid(testImmediateParentPostId.getValue()))
                .willReturn(Optional.of(createDraftPostEntityBuilderWithUuid().build()));

        // when
        boolean result = immediateParentPostRepositoryJpaAdapter.isImmediateParentPostPublished(testImmediateParentPostId);

        // then
        assertThat(result).isFalse();
        verify(postRepository).findByUlid(testImmediateParentPostId.getValue());
    }

    @Test
    @DisplayName("존재하지 않는 게시글의 발행 여부 확인 시 false 반환")
    void testIsImmediateParentPostPublished_givenNonExistentPost_willReturnFalse() {
        // given
        given(postRepository.findByUlid(testImmediateParentPostId.getValue())).willReturn(Optional.empty());

        // when
        boolean result = immediateParentPostRepositoryJpaAdapter.isImmediateParentPostPublished(testImmediateParentPostId);

        // then
        assertThat(result).isFalse();
        verify(postRepository).findByUlid(testImmediateParentPostId.getValue());
    }
}
