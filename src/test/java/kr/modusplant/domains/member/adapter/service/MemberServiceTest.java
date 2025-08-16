package kr.modusplant.domains.member.adapter.service;

import kr.modusplant.domains.member.adapter.mapper.MemberMapperImpl;
import kr.modusplant.domains.member.adapter.mapper.supers.MemberMapper;
import kr.modusplant.domains.member.adapter.repository.MemberRepository;
import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.framework.outbound.persistence.jpa.repository.MemberRepositoryImpl;
import kr.modusplant.domains.member.test.utils.adapter.MemberRequestUtils;
import kr.modusplant.domains.member.test.utils.domain.MemberUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberServiceTest implements MemberUtils, MemberRequestUtils {
    private final MemberMapper memberMapper = new MemberMapperImpl();
    private final MemberRepository memberRepository = Mockito.mock(MemberRepositoryImpl.class);
    private final MemberService memberService = new MemberService(memberMapper, memberRepository);

    @Test
    @DisplayName("register로 요청 등록")
    void callRegister_withValidRequest_returnsMember() {
        // given
        Member member = createMember();
        given(memberRepository.save(any())).willReturn(member);

        // when & then
        assertThat(memberService.register(testMemberRegisterRequest).getNickname()).isEqualTo(member.getNickname());
    }
}