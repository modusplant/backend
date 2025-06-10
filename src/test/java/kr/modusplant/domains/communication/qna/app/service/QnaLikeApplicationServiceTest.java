package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaLikeEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaLikeId;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaLikeRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.config.TestAopConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {TestAopConfig.class})
@Transactional
public class QnaLikeApplicationServiceTest implements SiteMemberEntityTestUtils, QnaPostEntityTestUtils {

    @Autowired
    private SiteMemberRepository siteMemberRepository;

    @Autowired
    private QnaPostRepository qnaPostRepository;

    @Autowired
    private QnaLikeRepository qnaLikeRepository;

    @Autowired
    private QnaLikeApplicationService qnaLikeApplicationService;

    private UUID memberId;
    private String postId;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        siteMemberRepository.save(member);
        memberId = member.getUuid();

        QnaPostEntity qnaPost = createQnaPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestQnaCategoryEntity())
                .build();
        qnaPostRepository.save(qnaPost);
        postId = qnaPost.getUlid();

        siteMemberRepository.flush();
        qnaPostRepository.flush();
    }

    @Test
    @DisplayName("좋아요 성공")
    void likeQnaPost_success() {
        // when
        LikeResponse response = qnaLikeApplicationService.likeQnaPost(postId, memberId);

        // then
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1);

        QnaLikeEntity saved = qnaLikeRepository.findById(new QnaLikeId(postId, memberId)).orElse(null);

        assertThat(saved).isNotNull();
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlikeQnaPost_success() {
        // given
        qnaLikeApplicationService.likeQnaPost(postId, memberId);

        // when
        LikeResponse response = qnaLikeApplicationService.unlikeQnaPost(postId, memberId);

        // then
        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isEqualTo(0);
        assertThat(qnaLikeRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
    }

    @Test
    @DisplayName("이미 좋아요 한 게시글을 또 좋아요 시도할 경우 예외 발생")
    void likeQnaPost_duplicateLike_throwsException() {
        // given
        qnaLikeApplicationService.likeQnaPost(postId, memberId);

        // when & then
        assertThatThrownBy(() ->
                qnaLikeApplicationService.likeQnaPost(postId, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member already liked.");
    }

    @Test
    @DisplayName("좋아요 하지 않은 게시글을 취소할 경우 예외 발생")
    void unlikeQnaPost_withoutLike_throwsException() {
        // when & then
        assertThatThrownBy(() ->
                qnaLikeApplicationService.unlikeQnaPost(postId, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member not liked.");
    }
}
