package kr.modusplant.domains.member.application.service;

import kr.modusplant.domains.member.adapter.in.mapper.supers.MemberMapper;
import kr.modusplant.domains.member.adapter.in.request.MemberNicknameUpdateRequest;
import kr.modusplant.domains.member.adapter.in.request.MemberRegisterRequest;
import kr.modusplant.domains.member.adapter.in.response.MemberResponse;
import kr.modusplant.domains.member.adapter.out.repository.MemberRepository;
import kr.modusplant.domains.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberApplicationService {
    private final MemberMapper mapper;
    private final MemberRepository memberRepository;

    public MemberResponse register(MemberRegisterRequest request) {
        Member member = Member.create(mapper.toNickname(request));
        return mapper.toMemberResponse(memberRepository.saveMember(member));
    }

    public MemberResponse updateNickname(MemberNicknameUpdateRequest request) {
        Member member = mapper.toMember(request);
        return mapper.toMemberResponse(memberRepository.updateNickname(member));
    }
}
