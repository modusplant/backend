package kr.modusplant.domains.communication.tip.app.service;

import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipLikeId;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipLikeRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
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
public class TipLikeApplicationServiceTest implements SiteMemberEntityTestUtils, TipPostEntityTestUtils {

    @Autowired
    private SiteMemberRepository siteMemberRepository;

    @Autowired
    private TipPostRepository tipPostRepository;

    @Autowired
    private TipLikeRepository tipLikeRepository;

    @Autowired
    private TipLikeApplicationService tipLikeApplicationService;

    private UUID memberId;
    private String postId;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        siteMemberRepository.save(member);
        memberId = member.getUuid();

        TipPostEntity tipPost = createTipPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestTipCategoryEntity())
                .build();
        tipPostRepository.save(tipPost);
        postId = tipPost.getUlid();

        siteMemberRepository.flush();
        tipPostRepository.flush();
    }

    @Test
    @DisplayName("좋아요 성공")
    void likeTipPost_success() {
        // when
        LikeResponse response = tipLikeApplicationService.likeTipPost(postId, memberId);

        // then
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1);

        TipLikeEntity saved = tipLikeRepository.findById(new TipLikeId(postId, memberId)).orElse(null);

        assertThat(saved).isNotNull();
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlikeTipPost_success() {
        // given
        tipLikeApplicationService.likeTipPost(postId, memberId);

        // when
        LikeResponse response = tipLikeApplicationService.unlikeTipPost(postId, memberId);

        // then
        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isEqualTo(0);
        assertThat(tipLikeRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
    }

    @Test
    @DisplayName("이미 좋아요 한 게시글을 또 좋아요 시도할 경우 예외 발생")
    void likeTipPost_duplicateLike_throwsException() {
        // given
        tipLikeApplicationService.likeTipPost(postId, memberId);

        // when & then
        assertThatThrownBy(() ->
                tipLikeApplicationService.likeTipPost(postId, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member already liked.");
    }

    @Test
    @DisplayName("좋아요 하지 않은 게시글을 취소할 경우 예외 발생")
    void unlikeTipPost_withoutLike_throwsException() {
        // when & then
        assertThatThrownBy(() ->
                tipLikeApplicationService.unlikeTipPost(postId, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Member not liked.");
    }
}
