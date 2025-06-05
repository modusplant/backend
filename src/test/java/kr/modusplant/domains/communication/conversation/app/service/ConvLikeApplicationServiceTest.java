package kr.modusplant.domains.communication.conversation.app.service;

import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvLikeEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvLikeId;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvLikeRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
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
public class ConvLikeApplicationServiceTest implements SiteMemberEntityTestUtils, ConvPostEntityTestUtils {

    @Autowired
    private SiteMemberRepository siteMemberRepository;

    @Autowired
    private ConvPostRepository convPostRepository;

    @Autowired
    private ConvLikeRepository convLikeRepository;

    @Autowired
    private ConvLikeApplicationService convLikeApplicationService;

    private UUID memberId;
    private String postId;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        siteMemberRepository.save(member);
        memberId = member.getUuid();

        ConvPostEntity convPost = createConvPostEntityBuilder()
                .authMember(member)
                .createMember(member)
                .category(createTestConvCategoryEntity())
                .build();
        convPostRepository.save(convPost);
        postId = convPost.getUlid();

        siteMemberRepository.flush();
        convPostRepository.flush();
    }

    @Test
    @DisplayName("좋아요 성공")
    void likeConvPost_success() {
        // when
        LikeResponse response = convLikeApplicationService.likeConvPost(postId, memberId);

        // then
        assertThat(response.liked()).isTrue();
        assertThat(response.likeCount()).isEqualTo(1);

        ConvLikeEntity saved = convLikeRepository.findById(new ConvLikeId(postId, memberId)).orElse(null);

        assertThat(saved).isNotNull();
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlikeConvPost_success() {
        // given
        convLikeApplicationService.likeConvPost(postId, memberId);

        // when
        LikeResponse response = convLikeApplicationService.unlikeConvPost(postId, memberId);

        // then
        assertThat(response.liked()).isFalse();
        assertThat(response.likeCount()).isEqualTo(0);
        assertThat(convLikeRepository.existsByPostIdAndMemberId(postId, memberId)).isFalse();
    }

    @Test
    @DisplayName("이미 좋아요 한 게시글을 또 좋아요 시도할 경우 예외 발생")
    void likeConvPost_duplicateLike_throwsException() {
        // given
        convLikeApplicationService.likeConvPost(postId, memberId);

        // when & then
        assertThatThrownBy(() ->
                convLikeApplicationService.likeConvPost(postId, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("member already liked");
    }

    @Test
    @DisplayName("좋아요 하지 않은 게시글을 취소할 경우 예외 발생")
    void unlikeConvPost_withoutLike_throwsException() {
        // when & then
        assertThatThrownBy(() ->
                convLikeApplicationService.unlikeConvPost(postId, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("member not liked status");
    }
}
