package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.domains.member.domain.vo.MemberStatus;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import kr.modusplant.domains.member.usecase.request.MemberNicknameUpdateRequest;
import kr.modusplant.domains.member.usecase.request.MemberRegisterRequest;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import org.springframework.stereotype.Component;

@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member toMember(MemberRegisterRequest request) {
        return Member.create(MemberNickname.create(request.nickname()));
    }

    @Override
    public Member toMember(MemberNicknameUpdateRequest request) {
        return Member.create(MemberId.fromUuid(request.id()), MemberStatus.fromBoolean(request.isActive()), MemberNickname.create(request.nickname()));
    }

    @Override
    public MemberResponse toMemberResponse(Member member) {
        return new MemberResponse(
                member.getMemberId().getValue(),
                member.getMemberStatus().getValue(),
                member.getMemberNickname().getValue(),
                member.getMemberBirthDate().getValue());
    }
}
