package kr.modusplant.domains.member.usecase.port.mapper;

import kr.modusplant.domains.member.adapter.request.MemberNicknameUpdateRequest;
import kr.modusplant.domains.member.adapter.request.MemberRegisterRequest;
import kr.modusplant.domains.member.adapter.response.MemberResponse;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberNickname;

public interface MemberMapper {
    MemberNickname toNickname(MemberRegisterRequest request);

    Member toMember(MemberNicknameUpdateRequest request);

    MemberResponse toMemberResponse(Member member);
}
