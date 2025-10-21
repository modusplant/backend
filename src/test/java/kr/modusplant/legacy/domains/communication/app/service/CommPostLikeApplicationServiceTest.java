package kr.modusplant.legacy.domains.communication.app.service;

import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostLikeEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.CommPostEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.CommPostLikeEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostLikeJpaRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.legacy.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.legacy.domains.communication.app.http.response.CommPostLikeResponse;
import kr.modusplant.legacy.domains.communication.domain.service.CommPostLikeValidationService;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import kr.modusplant.shared.persistence.compositekey.CommPostLikeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DomainsServiceWithoutValidationServiceContext
class CommPostLikeApplicationServiceTest implements SiteMemberEntityTestUtils, CommPostEntityTestUtils, CommPostLikeEntityTestUtils {

    private final SiteMemberJpaRepository siteMemberRepository;
    private final CommPostJpaRepository commPostRepository;
    private final CommPostLikeJpaRepository commPostLikeRepository;
    private final CommPostLikeApplicationService commPostLikeApplicationService;
    private final CommPostLikeValidationService commPostLikeValidationService;

    @Autowired
    public CommPostLikeApplicationServiceTest(SiteMemberJpaRepository siteMemberRepository, CommPostJpaRepository commPostRepository, CommPostLikeJpaRepository commPostLikeRepository, CommPostLikeApplicationService commPostLikeApplicationService, CommPostLikeValidationService commPostLikeValidationService) {
        this.siteMemberRepository = siteMemberRepository;
        this.commPostRepository = commPostRepository;
        this.commPostLikeRepository = commPostLikeRepository;
        this.commPostLikeApplicationService = commPostLikeApplicationService;
        this.commPostLikeValidationService = commPostLikeValidationService;
    }

    @Test
    @DisplayName("좋아요 성공")
    void likeCommPost_success() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        CommPostEntity commPost = createCommPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .secondaryCategory(createTestCommSecondaryCategoryEntity())
                .build();
        when(commPostRepository.save(commPost)).thenReturn(commPost);
        commPostRepository.save(commPost);
        String postId = commPost.getUlid();

        // when
        CommPostLikeEntity commLike = createCommPostLikeEntity();
        doNothing().when(commPostLikeValidationService).validateNotFoundCommPostLike(postId, memberId);
        doNothing().when(commPostLikeValidationService).validateExistedCommPostLike(postId, memberId);
        when(commPostRepository.findById(postId)).thenReturn(Optional.of(commPost));
        when(commPostLikeRepository.save(CommPostLikeEntity.of(postId, memberId))).thenReturn(commLike);
        CommPostLikeResponse response = commPostLikeApplicationService.likeCommPost(postId, memberId);

        // then
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1);

        when(commPostLikeRepository.findById(new CommPostLikeId(postId, memberId))).thenReturn(Optional.of(commLike));
        CommPostLikeEntity saved = commPostLikeRepository.findById(new CommPostLikeId(postId, memberId)).orElse(null);

        assertThat(saved).isNotNull();
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlikeCommPost_success() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        CommPostEntity commPost = createCommPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .secondaryCategory(createTestCommSecondaryCategoryEntity())
                .build();
        when(commPostRepository.save(commPost)).thenReturn(commPost);
        commPostRepository.save(commPost);
        String postId = commPost.getUlid();

        // when
        CommPostLikeEntity commLike = createCommPostLikeEntity();
        doNothing().when(commPostLikeValidationService).validateNotFoundCommPostLike(postId, memberId);
        doNothing().when(commPostLikeValidationService).validateExistedCommPostLike(postId, memberId);
        doNothing().when(commPostLikeValidationService).validateNotFoundCommPostOrMember(postId, memberId);
        when(commPostRepository.findById(postId)).thenReturn(Optional.of(commPost));
        when(commPostLikeRepository.save(CommPostLikeEntity.of(postId, memberId))).thenReturn(commLike);
        doNothing().when(commPostLikeRepository).deleteByPostIdAndMemberId(postId, memberId);
        commPostLikeApplicationService.likeCommPost(postId, memberId);
        CommPostLikeResponse response = commPostLikeApplicationService.unlikeCommPost(postId, memberId);

        // then
        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isEqualTo(0);
        when(commPostLikeRepository.existsByPostIdAndMemberId(postId, memberId)).thenReturn(false);
        assertThat(commPostLikeRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
    }

    @Test
    @DisplayName("이미 좋아요 한 게시글을 또 좋아요 시도할 경우 예외 발생")
    void likeCommPost_duplicateLike_willThrowException() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        CommPostEntity commPost = createCommPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .secondaryCategory(createTestCommSecondaryCategoryEntity())
                .build();
        when(commPostRepository.save(commPost)).thenReturn(commPost);
        commPostRepository.save(commPost);
        String postId = commPost.getUlid();

        // when
        CommPostLikeEntity commLike = createCommPostLikeEntity();
        doNothing().when(commPostLikeValidationService).validateNotFoundCommPostLike(postId, memberId);
        doNothing().when(commPostLikeValidationService).validateExistedCommPostLike(postId, memberId);
        when(commPostRepository.findById(postId)).thenReturn(Optional.of(commPost));
        when(commPostLikeRepository.save(CommPostLikeEntity.of(postId, memberId))).thenReturn(commLike);
        commPostLikeApplicationService.likeCommPost(postId, memberId);

        // then
        doNothing().when(commPostLikeValidationService).validateNotFoundCommPostOrMember(postId, memberId);
        doThrow(new EntityExistsException(ErrorCode.LIKE_EXISTS, EntityName.LIKE)).when(commPostLikeValidationService).validateExistedCommPostLike(postId, memberId);
        assertThatThrownBy(() -> commPostLikeApplicationService.likeCommPost(postId, memberId)).isInstanceOf(EntityExistsException.class);
    }

    @Test
    @DisplayName("좋아요 하지 않은 게시글을 취소할 경우 예외 발생")
    void unlikeCommPost_givenoutLike_willThrowException() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        CommPostEntity commPost = createCommPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .secondaryCategory(createTestCommSecondaryCategoryEntity())
                .build();
        when(commPostRepository.save(commPost)).thenReturn(commPost);
        commPostRepository.save(commPost);
        String postId = commPost.getUlid();

        // when
        CommPostLikeEntity commLike = createCommPostLikeEntity();
        doNothing().when(commPostLikeValidationService).validateNotFoundCommPostLike(postId, memberId);
        doNothing().when(commPostLikeValidationService).validateExistedCommPostLike(postId, memberId);
        when(commPostRepository.findById(postId)).thenReturn(Optional.of(commPost));
        when(commPostLikeRepository.save(CommPostLikeEntity.of(postId, memberId))).thenReturn(commLike);
        doNothing().when(commPostLikeRepository).deleteByPostIdAndMemberId(postId, memberId);
        commPostLikeApplicationService.likeCommPost(postId, memberId);
        commPostLikeApplicationService.unlikeCommPost(postId, memberId);

        // then
        doNothing().when(commPostLikeValidationService).validateNotFoundCommPostOrMember(postId, memberId);
        doThrow(new EntityNotFoundException(ErrorCode.LIKE_NOT_FOUND, EntityName.LIKE)).when(commPostLikeValidationService).validateNotFoundCommPostLike(postId, memberId);
        assertThatThrownBy(() -> commPostLikeApplicationService.unlikeCommPost(postId, memberId))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
