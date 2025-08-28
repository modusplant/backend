package kr.modusplant.domains.member.application.service;

import kr.modusplant.domains.member.adapter.in.mapper.MemberMapperImpl;
import kr.modusplant.domains.member.adapter.in.mapper.supers.MemberMapper;
import kr.modusplant.domains.member.adapter.out.repository.MemberRepository;
import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.framework.out.persistence.jpa.repository.MemberRepositoryJpaAdapter;
import kr.modusplant.domains.member.test.utils.adapter.MemberRequestTestUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberApplicationServiceTest implements MemberTestUtils, MemberRequestTestUtils {
    private final MemberMapper memberMapper = new MemberMapperImpl();
    private final MemberRepository memberRepository = Mockito.mock(MemberRepositoryJpaAdapter.class);
    private final MemberApplicationService memberService = new MemberApplicationService(memberMapper, memberRepository);

    @Test
    @DisplayName("updateNickname으로 닉네임 갱신")
    void callUpdateNickname_withValidRequest_returnsResponse() {
        // given
        Member member = createMember();
        given(memberRepository.updateNickname(any())).willReturn(member);

        // when & then
        assertThat(memberService.updateNickname(testMemberNicknameUpdateRequest).nickname()).isEqualTo(member.getMemberNickname().getValue());
    }

    @Test
    @DisplayName("register로 회원 등록")
    void callRegister_withValidRequest_returnsResponse() {
        // given
        Member member = createMember();
        given(memberRepository.save(any())).willReturn(member);

        // when & then
        assertThat(memberService.register(testMemberRegisterRequest).nickname()).isEqualTo(member.getMemberNickname().getValue());
    }
}