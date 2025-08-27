package kr.modusplant.domains.member.adapter.in.mapper.supers;

import kr.modusplant.domains.member.adapter.in.request.MemberNicknameUpdateRequest;
import kr.modusplant.domains.member.adapter.in.request.MemberRegisterRequest;
import kr.modusplant.domains.member.adapter.in.response.MemberResponse;
import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.domain.vo.MemberNickname;

public interface MemberMapper {
    MemberNickname toNickname(MemberRegisterRequest request);

    Member toMember(MemberNicknameUpdateRequest request);

    MemberResponse toMemberResponse(Member member);
}
