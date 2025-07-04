package kr.modusplant.domains.communication.tip.app.service;

import kr.modusplant.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.common.error.CommunicationExistsException;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.tip.common.util.entity.TipLikeEntityTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.communication.tip.domain.service.TipLikeValidationService;
import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeId;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipLikeRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DomainsServiceWithoutValidationServiceContext
class TipLikeApplicationServiceTest implements SiteMemberEntityTestUtils, TipPostEntityTestUtils, TipLikeEntityTestUtils {

    private final SiteMemberRepository siteMemberRepository;
    private final TipPostRepository tipPostRepository;
    private final TipLikeRepository tipLikeRepository;
    private final TipLikeApplicationService tipLikeApplicationService;
    private final TipLikeValidationService tipLikeValidationService;

    @Autowired
    public TipLikeApplicationServiceTest(SiteMemberRepository siteMemberRepository, TipPostRepository tipPostRepository, TipLikeRepository tipLikeRepository, TipLikeApplicationService tipLikeApplicationService, TipLikeValidationService tipLikeValidationService) {
        this.siteMemberRepository = siteMemberRepository;
        this.tipPostRepository = tipPostRepository;
        this.tipLikeRepository = tipLikeRepository;
        this.tipLikeApplicationService = tipLikeApplicationService;
        this.tipLikeValidationService = tipLikeValidationService;
    }

    @Test
    @DisplayName("좋아요 성공")
    void likeTipPost_success() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        TipPostEntity tipPost = createTipPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestTipCategoryEntity())
                .build();
        when(tipPostRepository.save(tipPost)).thenReturn(tipPost);
        tipPostRepository.save(tipPost);
        String postId = tipPost.getUlid();

        // when
        TipLikeEntity tipLike = createTipLikeEntity();
        doNothing().when(tipLikeValidationService).validateNotFoundTipLike(postId, memberId);
        doNothing().when(tipLikeValidationService).validateExistedTipLike(postId, memberId);
        when(tipPostRepository.findById(postId)).thenReturn(Optional.of(tipPost));
        when(tipLikeRepository.save(TipLikeEntity.of(postId, memberId))).thenReturn(tipLike);
        LikeResponse response = tipLikeApplicationService.likeTipPost(postId, memberId);

        // then
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1);

        when(tipLikeRepository.findById(new TipLikeId(postId, memberId))).thenReturn(Optional.of(tipLike));
        TipLikeEntity saved = tipLikeRepository.findById(new TipLikeId(postId, memberId)).orElse(null);

        assertThat(saved).isNotNull();
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlikeTipPost_success() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        TipPostEntity tipPost = createTipPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestTipCategoryEntity())
                .build();
        when(tipPostRepository.save(tipPost)).thenReturn(tipPost);
        tipPostRepository.save(tipPost);
        String postId = tipPost.getUlid();

        // when
        TipLikeEntity tipLike = createTipLikeEntity();
        doNothing().when(tipLikeValidationService).validateNotFoundTipLike(postId, memberId);
        doNothing().when(tipLikeValidationService).validateExistedTipLike(postId, memberId);
        doNothing().when(tipLikeValidationService).validateNotFoundTipPostOrMember(postId, memberId);
        when(tipPostRepository.findById(postId)).thenReturn(Optional.of(tipPost));
        when(tipLikeRepository.save(TipLikeEntity.of(postId, memberId))).thenReturn(tipLike);
        doNothing().when(tipLikeRepository).deleteByPostIdAndMemberId(postId, memberId);
        tipLikeApplicationService.likeTipPost(postId, memberId);
        LikeResponse response = tipLikeApplicationService.unlikeTipPost(postId, memberId);

        // then
        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isEqualTo(0);
        when(tipLikeRepository.existsByPostIdAndMemberId(postId, memberId)).thenReturn(false);
        assertThat(tipLikeRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
    }

    @Test
    @DisplayName("이미 좋아요 한 게시글을 또 좋아요 시도할 경우 예외 발생")
    void likeTipPost_duplicateLike_throwsException() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        TipPostEntity tipPost = createTipPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestTipCategoryEntity())
                .build();
        when(tipPostRepository.save(tipPost)).thenReturn(tipPost);
        tipPostRepository.save(tipPost);
        String postId = tipPost.getUlid();

        // when
        TipLikeEntity tipLike = createTipLikeEntity();
        doNothing().when(tipLikeValidationService).validateNotFoundTipLike(postId, memberId);
        doNothing().when(tipLikeValidationService).validateExistedTipLike(postId, memberId);
        when(tipPostRepository.findById(postId)).thenReturn(Optional.of(tipPost));
        when(tipLikeRepository.save(TipLikeEntity.of(postId, memberId))).thenReturn(tipLike);
        tipLikeApplicationService.likeTipPost(postId, memberId);

        // then
        doNothing().when(tipLikeValidationService).validateNotFoundTipPostOrMember(postId, memberId);
        doThrow(CommunicationExistsException.ofLike()).when(tipLikeValidationService).validateExistedTipLike(postId, memberId);
        assertThatThrownBy(() -> tipLikeApplicationService.likeTipPost(postId, memberId)).isInstanceOf(CommunicationExistsException.class);
    }

    @Test
    @DisplayName("좋아요 하지 않은 게시글을 취소할 경우 예외 발생")
    void unlikeTipPost_withoutLike_throwsException() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        TipPostEntity tipPost = createTipPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestTipCategoryEntity())
                .build();
        when(tipPostRepository.save(tipPost)).thenReturn(tipPost);
        tipPostRepository.save(tipPost);
        String postId = tipPost.getUlid();

        // when
        TipLikeEntity tipLike = createTipLikeEntity();
        doNothing().when(tipLikeValidationService).validateNotFoundTipLike(postId, memberId);
        doNothing().when(tipLikeValidationService).validateExistedTipLike(postId, memberId);
        when(tipPostRepository.findById(postId)).thenReturn(Optional.of(tipPost));
        when(tipLikeRepository.save(TipLikeEntity.of(postId, memberId))).thenReturn(tipLike);
        doNothing().when(tipLikeRepository).deleteByPostIdAndMemberId(postId, memberId);
        tipLikeApplicationService.likeTipPost(postId, memberId);
        tipLikeApplicationService.unlikeTipPost(postId, memberId);

        // then
        doNothing().when(tipLikeValidationService).validateNotFoundTipPostOrMember(postId, memberId);
        doThrow(CommunicationNotFoundException.ofLike()).when(tipLikeValidationService).validateNotFoundTipLike(postId, memberId);
        assertThatThrownBy(() -> tipLikeApplicationService.unlikeTipPost(postId, memberId))
                .isInstanceOf(CommunicationNotFoundException.class);
    }
}
