package kr.modusplant.domains.comment.framework.outbound.jpa.repository;

import kr.modusplant.domains.comment.common.util.domain.vo.ParentPostIdTestUtils;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PostEntityTestUtils;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class ParentPostRepositoryJpaAdapterTest implements ParentPostIdTestUtils, PostEntityTestUtils {
    private final PostJpaRepository postRepository = Mockito.mock(PostJpaRepository.class);
    private final ParentPostRepositoryJpaAdapter parentPostRepositoryJpaAdapter =
            new ParentPostRepositoryJpaAdapter(postRepository);

    @Test
    @DisplayName("존재하는 게시글 여부 확인 시 true 반환")
    void testIsParentPostExists_givenExistingPost_willReturnTrue() {
        // given
        given(postRepository.existsByUlid(testParentPostId.getValue())).willReturn(true);

        // when
        boolean result = parentPostRepositoryJpaAdapter.isParentPostExists(testParentPostId);

        // then
        assertThat(result).isTrue();
        verify(postRepository).existsByUlid(testParentPostId.getValue());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 여부 확인 시 false 반환")
    void testIsParentPostExists_givenNonExistentPost_willReturnFalse() {
        // given
        given(postRepository.existsByUlid(testParentPostId.getValue())).willReturn(false);

        // when
        boolean result = parentPostRepositoryJpaAdapter.isParentPostExists(testParentPostId);

        // then
        assertThat(result).isFalse();
        verify(postRepository).existsByUlid(testParentPostId.getValue());
    }

    @Test
    @DisplayName("발행된 게시글 여부 확인 시 true 반환")
    void testIsParentPostPublished_givenPublishedPost_willReturnTrue() {
        // given
        given(postRepository.findByUlid(testParentPostId.getValue()))
                .willReturn(Optional.of(createPublishedPostEntityBuilderWithUuid().build()));

        // when
        boolean result = parentPostRepositoryJpaAdapter.isParentPostPublished(testParentPostId);

        // then
        assertThat(result).isTrue();
        verify(postRepository).findByUlid(testParentPostId.getValue());
    }

    @Test
    @DisplayName("발행되지 않은 게시글 여부 확인 시 false 반환")
    void testIsParentPostPublished_givenDraftPost_willReturnFalse() {
        // given
        given(postRepository.findByUlid(testParentPostId.getValue()))
                .willReturn(Optional.of(createDraftPostEntityBuilderWithUuid().build()));

        // when
        boolean result = parentPostRepositoryJpaAdapter.isParentPostPublished(testParentPostId);

        // then
        assertThat(result).isFalse();
        verify(postRepository).findByUlid(testParentPostId.getValue());
    }

    @Test
    @DisplayName("존재하지 않는 게시글의 발행 여부 확인 시 false 반환")
    void testIsParentPostPublished_givenNonExistentPost_willReturnFalse() {
        // given
        given(postRepository.findByUlid(testParentPostId.getValue())).willReturn(Optional.empty());

        // when
        boolean result = parentPostRepositoryJpaAdapter.isParentPostPublished(testParentPostId);

        // then
        assertThat(result).isFalse();
        verify(postRepository).findByUlid(testParentPostId.getValue());
    }
}
