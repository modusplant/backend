package kr.modusplant.domains.member.adapter.mapper.supers;

import kr.modusplant.domains.member.adapter.request.MemberRegisterRequest;
import kr.modusplant.domains.member.adapter.response.MemberResponse;
import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.domain.vo.Nickname;

public interface MemberMapper {
    Nickname toNickname(MemberRegisterRequest request);

    MemberResponse toMemberResponse(Member member);
}
