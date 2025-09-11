package kr.modusplant.domains.member.framework.out.jpa.mapper;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberBirthDate;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.domains.member.domain.vo.MemberStatus;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.mapper.supers.MemberJpaMapper;
import org.springframework.stereotype.Component;

@Component
public class MemberJpaMapperImpl implements MemberJpaMapper {

    @Override
    public MemberEntity toMemberEntity(Member member) {
        return MemberEntity.builder().uuid(member.getMemberId().getValue()).isActive(member.getMemberStatus().isActive()).nickname(member.getMemberNickname().getValue()).birthDate(member.getMemberBirthDate().getValue()).build();
    }

    @Override
    public Member toMember(MemberEntity entity) {
        MemberStatus status;
        if (entity.getIsActive()) {
            status = MemberStatus.active();
        } else {
            status = MemberStatus.inactive();
        }
        return Member.create(MemberId.fromUuid(entity.getUuid()), status, MemberNickname.create(entity.getNickname()), MemberBirthDate.create(entity.getBirthDate()));
    }
}
