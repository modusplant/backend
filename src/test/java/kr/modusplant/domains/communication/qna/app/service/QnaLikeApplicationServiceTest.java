package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.common.error.LikeExistsException;
import kr.modusplant.domains.communication.common.error.LikeNotFoundException;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaLikeEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.domain.service.QnaLikeValidationService;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaLikeEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaLikeId;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaLikeRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
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
class QnaLikeApplicationServiceTest implements SiteMemberEntityTestUtils, QnaPostEntityTestUtils, QnaLikeEntityTestUtils {

    private final SiteMemberRepository siteMemberRepository;
    private final QnaPostRepository qnaPostRepository;
    private final QnaLikeRepository qnaLikeRepository;
    private final QnaLikeApplicationService qnaLikeApplicationService;
    private final QnaLikeValidationService qnaLikeValidationService;

    @Autowired
    public QnaLikeApplicationServiceTest(SiteMemberRepository siteMemberRepository, QnaPostRepository qnaPostRepository, QnaLikeRepository qnaLikeRepository, QnaLikeApplicationService qnaLikeApplicationService, QnaLikeValidationService qnaLikeValidationService) {
        this.siteMemberRepository = siteMemberRepository;
        this.qnaPostRepository = qnaPostRepository;
        this.qnaLikeRepository = qnaLikeRepository;
        this.qnaLikeApplicationService = qnaLikeApplicationService;
        this.qnaLikeValidationService = qnaLikeValidationService;
    }

    @Test
    @DisplayName("좋아요 성공")
    void likeQnaPost_success() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        QnaPostEntity qnaPost = createQnaPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestQnaCategoryEntity())
                .build();
        when(qnaPostRepository.save(qnaPost)).thenReturn(qnaPost);
        qnaPostRepository.save(qnaPost);
        String postId = qnaPost.getUlid();

        // when
        QnaLikeEntity qnaLike = createQnaLikeEntity();
        doNothing().when(qnaLikeValidationService).validateNotFoundQnaLike(postId, memberId);
        doNothing().when(qnaLikeValidationService).validateExistedQnaLike(postId, memberId);
        when(qnaPostRepository.findById(postId)).thenReturn(Optional.of(qnaPost));
        when(qnaLikeRepository.save(QnaLikeEntity.of(postId, memberId))).thenReturn(qnaLike);
        LikeResponse response = qnaLikeApplicationService.likeQnaPost(postId, memberId);

        // then
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1);

        when(qnaLikeRepository.findById(new QnaLikeId(postId, memberId))).thenReturn(Optional.of(qnaLike));
        QnaLikeEntity saved = qnaLikeRepository.findById(new QnaLikeId(postId, memberId)).orElse(null);

        assertThat(saved).isNotNull();
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlikeQnaPost_success() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        QnaPostEntity qnaPost = createQnaPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestQnaCategoryEntity())
                .build();
        when(qnaPostRepository.save(qnaPost)).thenReturn(qnaPost);
        qnaPostRepository.save(qnaPost);
        String postId = qnaPost.getUlid();

        // when
        QnaLikeEntity qnaLike = createQnaLikeEntity();
        doNothing().when(qnaLikeValidationService).validateNotFoundQnaLike(postId, memberId);
        doNothing().when(qnaLikeValidationService).validateExistedQnaLike(postId, memberId);
        doNothing().when(qnaLikeValidationService).validateNotFoundQnaPostOrMember(postId, memberId);
        when(qnaPostRepository.findById(postId)).thenReturn(Optional.of(qnaPost));
        when(qnaLikeRepository.save(QnaLikeEntity.of(postId, memberId))).thenReturn(qnaLike);
        doNothing().when(qnaLikeRepository).deleteByPostIdAndMemberId(postId, memberId);
        qnaLikeApplicationService.likeQnaPost(postId, memberId);
        LikeResponse response = qnaLikeApplicationService.unlikeQnaPost(postId, memberId);

        // then
        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isEqualTo(0);
        when(qnaLikeRepository.existsByPostIdAndMemberId(postId, memberId)).thenReturn(false);
        assertThat(qnaLikeRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
    }

    @Test
    @DisplayName("이미 좋아요 한 게시글을 또 좋아요 시도할 경우 예외 발생")
    void likeQnaPost_duplicateLike_throwsException() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        QnaPostEntity qnaPost = createQnaPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestQnaCategoryEntity())
                .build();
        when(qnaPostRepository.save(qnaPost)).thenReturn(qnaPost);
        qnaPostRepository.save(qnaPost);
        String postId = qnaPost.getUlid();

        // when
        QnaLikeEntity qnaLike = createQnaLikeEntity();
        doNothing().when(qnaLikeValidationService).validateNotFoundQnaLike(postId, memberId);
        doNothing().when(qnaLikeValidationService).validateExistedQnaLike(postId, memberId);
        when(qnaPostRepository.findById(postId)).thenReturn(Optional.of(qnaPost));
        when(qnaLikeRepository.save(QnaLikeEntity.of(postId, memberId))).thenReturn(qnaLike);
        qnaLikeApplicationService.likeQnaPost(postId, memberId);

        // then
        doNothing().when(qnaLikeValidationService).validateNotFoundQnaPostOrMember(postId, memberId);
        doThrow(new LikeExistsException()).when(qnaLikeValidationService).validateExistedQnaLike(postId, memberId);
        assertThatThrownBy(() -> qnaLikeApplicationService.likeQnaPost(postId, memberId)).isInstanceOf(LikeExistsException.class);
    }

    @Test
    @DisplayName("좋아요 하지 않은 게시글을 취소할 경우 예외 발생")
    void unlikeQnaPost_withoutLike_throwsException() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();
        when(siteMemberRepository.save(member)).thenReturn(member);
        siteMemberRepository.save(member);
        UUID memberId = member.getUuid();

        QnaPostEntity qnaPost = createQnaPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestQnaCategoryEntity())
                .build();
        when(qnaPostRepository.save(qnaPost)).thenReturn(qnaPost);
        qnaPostRepository.save(qnaPost);
        String postId = qnaPost.getUlid();

        // when
        QnaLikeEntity qnaLike = createQnaLikeEntity();
        doNothing().when(qnaLikeValidationService).validateNotFoundQnaLike(postId, memberId);
        doNothing().when(qnaLikeValidationService).validateExistedQnaLike(postId, memberId);
        when(qnaPostRepository.findById(postId)).thenReturn(Optional.of(qnaPost));
        when(qnaLikeRepository.save(QnaLikeEntity.of(postId, memberId))).thenReturn(qnaLike);
        doNothing().when(qnaLikeRepository).deleteByPostIdAndMemberId(postId, memberId);
        qnaLikeApplicationService.likeQnaPost(postId, memberId);
        qnaLikeApplicationService.unlikeQnaPost(postId, memberId);

        // then
        doNothing().when(qnaLikeValidationService).validateNotFoundQnaPostOrMember(postId, memberId);
        doThrow(new LikeNotFoundException()).when(qnaLikeValidationService).validateNotFoundQnaLike(postId, memberId);
        assertThatThrownBy(() -> qnaLikeApplicationService.unlikeQnaPost(postId, memberId))
                .isInstanceOf(LikeNotFoundException.class);
    }
}
