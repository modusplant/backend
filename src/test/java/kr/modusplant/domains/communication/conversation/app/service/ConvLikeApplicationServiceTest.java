package kr.modusplant.domains.communication.conversation.app.service;

import kr.modusplant.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.common.error.CommunicationExistsException;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvLikeEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.communication.conversation.domain.service.ConvLikeValidationService;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvLikeEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvLikeId;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvLikeRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
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
class ConvLikeApplicationServiceTest implements SiteMemberEntityTestUtils, ConvPostEntityTestUtils, ConvLikeEntityTestUtils {

    private final SiteMemberRepository siteMemberRepository;
    private final ConvPostRepository convPostRepository;
    private final ConvLikeRepository convLikeRepository;
    private final ConvLikeApplicationService convLikeApplicationService;
    private final ConvLikeValidationService convLikeValidationService;

    @Autowired
    public ConvLikeApplicationServiceTest(SiteMemberRepository siteMemberRepository, ConvPostRepository convPostRepository, ConvLikeRepository convLikeRepository, ConvLikeApplicationService convLikeApplicationService, ConvLikeValidationService convLikeValidationService) {
        this.siteMemberRepository = siteMemberRepository;
        this.convPostRepository = convPostRepository;
        this.convLikeRepository = convLikeRepository;
        this.convLikeApplicationService = convLikeApplicationService;
        this.convLikeValidationService = convLikeValidationService;
    }

    @Test
    @DisplayName("좋아요 성공")
    void likeConvPost_success() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        ConvPostEntity convPost = createConvPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestConvCategoryEntity())
                .build();
        when(convPostRepository.save(convPost)).thenReturn(convPost);
        convPostRepository.save(convPost);
        String postId = convPost.getUlid();

        // when
        ConvLikeEntity convLike = createConvLikeEntity();
        doNothing().when(convLikeValidationService).validateNotFoundConvLike(postId, memberId);
        doNothing().when(convLikeValidationService).validateExistedConvLike(postId, memberId);
        when(convPostRepository.findById(postId)).thenReturn(Optional.of(convPost));
        when(convLikeRepository.save(ConvLikeEntity.of(postId, memberId))).thenReturn(convLike);
        LikeResponse response = convLikeApplicationService.likeConvPost(postId, memberId);

        // then
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1);

        when(convLikeRepository.findById(new ConvLikeId(postId, memberId))).thenReturn(Optional.of(convLike));
        ConvLikeEntity saved = convLikeRepository.findById(new ConvLikeId(postId, memberId)).orElse(null);

        assertThat(saved).isNotNull();
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlikeConvPost_success() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        ConvPostEntity convPost = createConvPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestConvCategoryEntity())
                .build();
        when(convPostRepository.save(convPost)).thenReturn(convPost);
        convPostRepository.save(convPost);
        String postId = convPost.getUlid();

        // when
        ConvLikeEntity convLike = createConvLikeEntity();
        doNothing().when(convLikeValidationService).validateNotFoundConvLike(postId, memberId);
        doNothing().when(convLikeValidationService).validateExistedConvLike(postId, memberId);
        doNothing().when(convLikeValidationService).validateNotFoundConvPostOrMember(postId, memberId);
        when(convPostRepository.findById(postId)).thenReturn(Optional.of(convPost));
        when(convLikeRepository.save(ConvLikeEntity.of(postId, memberId))).thenReturn(convLike);
        doNothing().when(convLikeRepository).deleteByPostIdAndMemberId(postId, memberId);
        convLikeApplicationService.likeConvPost(postId, memberId);
        LikeResponse response = convLikeApplicationService.unlikeConvPost(postId, memberId);

        // then
        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isEqualTo(0);
        when(convLikeRepository.existsByPostIdAndMemberId(postId, memberId)).thenReturn(false);
        assertThat(convLikeRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
    }

    @Test
    @DisplayName("이미 좋아요 한 게시글을 또 좋아요 시도할 경우 예외 발생")
    void likeConvPost_duplicateLike_throwsException() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        ConvPostEntity convPost = createConvPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestConvCategoryEntity())
                .build();
        when(convPostRepository.save(convPost)).thenReturn(convPost);
        convPostRepository.save(convPost);
        String postId = convPost.getUlid();

        // when
        ConvLikeEntity convLike = createConvLikeEntity();
        doNothing().when(convLikeValidationService).validateNotFoundConvLike(postId, memberId);
        doNothing().when(convLikeValidationService).validateExistedConvLike(postId, memberId);
        when(convPostRepository.findById(postId)).thenReturn(Optional.of(convPost));
        when(convLikeRepository.save(ConvLikeEntity.of(postId, memberId))).thenReturn(convLike);
        convLikeApplicationService.likeConvPost(postId, memberId);

        // then
        doNothing().when(convLikeValidationService).validateNotFoundConvPostOrMember(postId, memberId);
        doThrow(CommunicationExistsException.ofLike()).when(convLikeValidationService).validateExistedConvLike(postId, memberId);
        assertThatThrownBy(() -> convLikeApplicationService.likeConvPost(postId, memberId)).isInstanceOf(CommunicationExistsException.class);
    }

    @Test
    @DisplayName("좋아요 하지 않은 게시글을 취소할 경우 예외 발생")
    void unlikeConvPost_withoutLike_throwsException() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        ConvPostEntity convPost = createConvPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestConvCategoryEntity())
                .build();
        when(convPostRepository.save(convPost)).thenReturn(convPost);
        convPostRepository.save(convPost);
        String postId = convPost.getUlid();

        // when
        ConvLikeEntity convLike = createConvLikeEntity();
        doNothing().when(convLikeValidationService).validateNotFoundConvLike(postId, memberId);
        doNothing().when(convLikeValidationService).validateExistedConvLike(postId, memberId);
        when(convPostRepository.findById(postId)).thenReturn(Optional.of(convPost));
        when(convLikeRepository.save(ConvLikeEntity.of(postId, memberId))).thenReturn(convLike);
        doNothing().when(convLikeRepository).deleteByPostIdAndMemberId(postId, memberId);
        convLikeApplicationService.likeConvPost(postId, memberId);
        convLikeApplicationService.unlikeConvPost(postId, memberId);

        // then
        doNothing().when(convLikeValidationService).validateNotFoundConvPostOrMember(postId, memberId);
        doThrow(CommunicationNotFoundException.ofLike()).when(convLikeValidationService).validateNotFoundConvLike(postId, memberId);
        assertThatThrownBy(() -> convLikeApplicationService.unlikeConvPost(postId, memberId))
                .isInstanceOf(CommunicationNotFoundException.class);
    }
}
