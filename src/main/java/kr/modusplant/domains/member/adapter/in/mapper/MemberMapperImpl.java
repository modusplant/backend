package kr.modusplant.domains.member.adapter.in.mapper;

import kr.modusplant.domains.member.adapter.in.mapper.supers.MemberMapper;
import kr.modusplant.domains.member.adapter.in.request.MemberRegisterRequest;
import kr.modusplant.domains.member.adapter.in.response.MemberResponse;
import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.domain.vo.Nickname;
import org.springframework.stereotype.Component;

@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Nickname toNickname(MemberRegisterRequest request) {
        return Nickname.of(request.nickname());
    }

    @Override
    public MemberResponse toMemberResponse(Member member) {
        return new MemberResponse(
                member.getMemberId().getValue(),
                member.getMemberStatus().getStatus().getValue(),
                member.getNickname().getValue(),
                member.getBirthDate().getValue());
    }
}
