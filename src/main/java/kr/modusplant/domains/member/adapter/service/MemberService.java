package kr.modusplant.domains.member.adapter.service;

import kr.modusplant.domains.member.adapter.mapper.supers.MemberMapper;
import kr.modusplant.domains.member.adapter.repository.MemberRepository;
import kr.modusplant.domains.member.adapter.request.MemberRegisterRequest;
import kr.modusplant.domains.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberMapper mapper;
    private final MemberRepository memberRepository;

    public Member register(MemberRegisterRequest request) {
        Member member = Member.create(mapper.toNickname(request));
        return memberRepository.save(member);
    }
}
