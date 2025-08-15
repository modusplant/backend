package kr.modusplant.domains.member.adapter.presenter;

import kr.modusplant.domains.member.adapter.mapper.supers.MemberMapper;
import kr.modusplant.domains.member.adapter.response.MemberResponse;
import kr.modusplant.domains.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberPresenter {
    private final MemberMapper mapper;

    public MemberResponse presentMemberResponse(Member member) {
        return mapper.toMemberResponse(member);
    }
}
