package kr.modusplant.domains.member.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.member.domain.entity.Member;
import kr.modusplant.domains.member.domain.vo.BirthDate;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberStatus;
import kr.modusplant.domains.member.domain.vo.Nickname;
import kr.modusplant.domains.member.framework.out.persistence.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.persistence.jpa.mapper.supers.MemberJpaMapper;
import org.springframework.stereotype.Component;

@Component
public class MemberJpaMapperImpl implements MemberJpaMapper {

    @Override
    public MemberEntity toMemberEntity(Member member) {
        return MemberEntity.builder().uuid(member.getMemberId().getValue()).isActive(member.getMemberStatus().isActive()).nickname(member.getNickname().getValue()).birthDate(member.getBirthDate().getValue()).build();
    }

    @Override
    public Member toMember(MemberEntity entity) {
        MemberStatus status;
        if (entity.getIsActive()) {
            status = MemberStatus.active();
        } else {
            status = MemberStatus.inactive();
        }
        return Member.create(MemberId.fromUuid(entity.getUuid()), status, Nickname.of(entity.getNickname()), BirthDate.of(entity.getBirthDate()));
    }
}
