package kr.modusplant.domains.member.framework.out.jpa.mapper;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberBirthDate;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberStatus;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberBirthDate;
import kr.modusplant.domains.member.framework.out.jpa.mapper.supers.MemberJpaMapper;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.kernel.Nickname;
import org.springframework.stereotype.Component;

@Component
public class MemberJpaMapperImpl implements MemberJpaMapper {

    @Override
    public SiteMemberEntity toMemberEntity(Nickname nickname) {
        return SiteMemberEntity.builder().nickname(nickname.getValue()).build();
    }

    @Override
    public SiteMemberEntity toMemberEntity(MemberId memberId, Nickname nickname) {
        return SiteMemberEntity.builder().uuid(memberId.getValue()).nickname(nickname.getValue()).build();
    }

    @Override
    public Member toMember(SiteMemberEntity entity) {
        MemberStatus status;
        if (entity.getIsActive()) {
            status = MemberStatus.active();
        } else {
            status = MemberStatus.inactive();
        }
        MemberBirthDate birthDate;
        if (entity.getBirthDate() == null) {
            birthDate = EmptyMemberBirthDate.create();
        } else {
            birthDate = MemberBirthDate.create(entity.getBirthDate());
        }
        return Member.create(
                MemberId.fromUuid(entity.getUuid()),
                status,
                Nickname.create(entity.getNickname()),
                birthDate
        );
    }
}
