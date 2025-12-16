package kr.modusplant.domains.member.adapter.mapper;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import org.springframework.stereotype.Component;

@Component
public class MemberMapperImpl implements MemberMapper {
    @Override
    public MemberResponse toMemberResponse(Member member) {
        return new MemberResponse(
                member.getMemberId().getValue(),
                member.getMemberStatus().getValue(),
                member.getNickname().getValue(),
                member.getMemberBirthDate().getValue());
    }
}
