package kr.modusplant.domains.member.adapter.in.mapper;

import kr.modusplant.domains.member.adapter.in.mapper.supers.MemberMapper;
import kr.modusplant.domains.member.adapter.in.request.MemberNicknameUpdateRequest;
import kr.modusplant.domains.member.adapter.in.request.MemberRegisterRequest;
import kr.modusplant.domains.member.adapter.in.response.MemberResponse;
import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.domains.member.domain.vo.MemberStatus;
import org.springframework.stereotype.Component;

@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public MemberNickname toNickname(MemberRegisterRequest request) {
        return MemberNickname.of(request.nickname());
    }

    @Override
    public Member toMember(MemberNicknameUpdateRequest request) {
        return Member.create(MemberId.fromUuid(request.id()), MemberStatus.fromBoolean(request.isActive()), MemberNickname.of(request.nickname()));
    }

    @Override
    public MemberResponse toMemberResponse(Member member) {
        return new MemberResponse(
                member.getMemberId().getValue(),
                member.getMemberStatus().getStatus().getValue(),
                member.getMemberNickname().getValue(),
                member.getMemberBirthDate().getValue());
    }
}
