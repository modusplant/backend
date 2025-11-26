package kr.modusplant.domains.member.framework.out.jpa.mapper;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberBirthDate;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.domains.member.domain.vo.MemberStatus;
import kr.modusplant.domains.member.domain.vo.nullobject.MemberEmptyBirthDate;
import kr.modusplant.domains.member.framework.out.jpa.mapper.supers.MemberJpaMapper;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberJpaMapperImpl implements MemberJpaMapper {

    @Override
    public SiteMemberEntity toMemberEntity(MemberNickname memberNickname) {
        return SiteMemberEntity.builder().nickname(memberNickname.getValue()).build();
    }

    @Override
    public SiteMemberEntity toMemberEntity(MemberId memberId, MemberNickname memberNickname) {
        return SiteMemberEntity.builder().uuid(memberId.getValue()).nickname(memberNickname.getValue()).build();
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
            birthDate = MemberEmptyBirthDate.create();
        } else {
            birthDate = MemberBirthDate.create(entity.getBirthDate());
        }
        return Member.create(MemberId.fromUuid(entity.getUuid()), status, MemberNickname.create(entity.getNickname()), birthDate);
    }
}
