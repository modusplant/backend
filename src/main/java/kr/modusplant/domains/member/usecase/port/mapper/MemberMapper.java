package kr.modusplant.domains.member.usecase.port.mapper;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.domains.member.usecase.request.MemberNicknameUpdateRequest;
import kr.modusplant.domains.member.usecase.response.MemberResponse;

public interface MemberMapper {
    Member toMember(MemberNickname nickname);

    Member toMember(MemberNicknameUpdateRequest request);

    MemberResponse toMemberResponse(Member member);
}
