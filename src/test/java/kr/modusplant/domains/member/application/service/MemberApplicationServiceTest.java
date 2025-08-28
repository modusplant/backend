package kr.modusplant.domains.member.application.service;

import kr.modusplant.domains.member.adapter.in.mapper.MemberMapperImpl;
import kr.modusplant.domains.member.adapter.in.mapper.supers.MemberMapper;
import kr.modusplant.domains.member.adapter.out.repository.MemberRepository;
import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.framework.out.persistence.jpa.repository.MemberRepositoryImpl;
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
    private final MemberRepository memberRepository = Mockito.mock(MemberRepositoryImpl.class);
    private final MemberApplicationService memberService = new MemberApplicationService(memberMapper, memberRepository);

    @Test
    @DisplayName("register로 요청 등록")
    void callRegister_withValidRequest_returnsMember() {
        // given
        Member member = createMember();
        given(memberRepository.save(any())).willReturn(member);

        // when & then
        assertThat(memberService.register(testMemberRegisterRequest).nickname()).isEqualTo(member.getNickname().getValue());
    }
}