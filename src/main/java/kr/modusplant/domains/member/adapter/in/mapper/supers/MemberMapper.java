package kr.modusplant.domains.member.adapter.in.mapper.supers;

import kr.modusplant.domains.member.adapter.in.request.MemberRegisterRequest;
import kr.modusplant.domains.member.adapter.in.response.MemberResponse;
import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.domain.vo.Nickname;

public interface MemberMapper {
    Nickname toNickname(MemberRegisterRequest request);

    MemberResponse toMemberResponse(Member member);
}
