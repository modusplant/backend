package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.request.MemberNicknameUpdateRequest;
import kr.modusplant.domains.member.adapter.request.MemberRegisterRequest;
import kr.modusplant.domains.member.adapter.response.MemberResponse;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberController {
    private final MemberMapper mapper;
    private final MemberRepository memberRepository;

    public MemberResponse register(MemberRegisterRequest request) {
        Member member = Member.create(mapper.toNickname(request));
        return mapper.toMemberResponse(memberRepository.save(member));
    }

    public MemberResponse updateNickname(MemberNicknameUpdateRequest request) {
        Member member = mapper.toMember(request);
        return mapper.toMemberResponse(memberRepository.updateNickname(member));
    }
}
