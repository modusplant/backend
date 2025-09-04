package kr.modusplant.domains.member.application.service;

import kr.modusplant.domains.member.adapter.controller.MemberController;
import kr.modusplant.domains.member.adapter.mapper.MemberMapperImpl;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.framework.out.persistence.jpa.repository.MemberRepositoryJpaAdapter;
import kr.modusplant.domains.member.test.utils.adapter.MemberRequestTestUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberTestUtils;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.framework.out.persistence.jpa.entity.CommLikeEntity;
import kr.modusplant.framework.out.persistence.jpa.repository.CommLikeRepository;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.infrastructure.event.consumer.PostEventConsumer;
import kr.modusplant.shared.event.CommPostLikeEventTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

class MemberControllerTest implements MemberTestUtils, MemberRequestTestUtils, CommPostLikeEventTestUtils {
    private final MemberMapper memberMapper = new MemberMapperImpl();
    private final MemberRepository memberRepository = Mockito.mock(MemberRepositoryJpaAdapter.class);
    private final CommLikeRepository commLikeRepository = Mockito.mock(CommLikeRepository.class);
    private final EventBus eventBus = new EventBus();
    private final PostEventConsumer postEventConsumer = new PostEventConsumer(eventBus, commLikeRepository);
    private final MemberController memberController = new MemberController(memberMapper, memberRepository, eventBus);

    @Test
    @DisplayName("updateNickname으로 닉네임 갱신")
    void callUpdateNickname_withValidRequest_returnsResponse() {
        // given
        Member member = createMember();
        given(memberRepository.updateNickname(any())).willReturn(member);

        // when & then
        assertThat(memberController.updateNickname(testMemberNicknameUpdateRequest).nickname()).isEqualTo(member.getMemberNickname().getValue());
    }

    @Test
    @DisplayName("register로 회원 등록")
    void callRegister_withValidRequest_returnsResponse() {
        // given
        Member member = createMember();
        given(memberRepository.save(any())).willReturn(member);

        // when & then
        assertThat(memberController.register(testMemberRegisterRequest).nickname()).isEqualTo(member.getMemberNickname().getValue());
    }

    @Test
    @DisplayName("likePost로 게시글 좋아요")
    void callLikePost_withValidParameter_returnsVoid() {
        // given
        UUID memberId = testCommPostLikeEvent.getMemberId();
        String postId = testCommPostLikeEvent.getPostId();
        CommLikeEntity entity = CommLikeEntity.of(postId, memberId);
        given(commLikeRepository.save(entity)).willReturn(entity);

        // when
        memberController.likePost(memberId, postId);

        // then
        verify(commLikeRepository, atLeastOnce()).save(any());
    }

    @Test
    @DisplayName("unlikePost로 게시글 좋아요")
    void callUnlikePost_withValidParameter_returnsVoid() {
        // given
        UUID memberId = testCommPostLikeEvent.getMemberId();
        String postId = testCommPostLikeEvent.getPostId();
        CommLikeEntity entity = CommLikeEntity.of(postId, memberId);
        willDoNothing().given(commLikeRepository).delete(entity);

        // when
        memberController.unlikePost(memberId, postId);

        // then
        verify(commLikeRepository, atLeastOnce()).delete(any());
    }
}