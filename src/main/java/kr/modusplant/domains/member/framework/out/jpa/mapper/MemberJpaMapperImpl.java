package kr.modusplant.domains.member.framework.out.jpa.mapper;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberBirthDate;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.domains.member.domain.vo.MemberStatus;
import kr.modusplant.domains.member.framework.out.jpa.mapper.supers.MemberJpaMapper;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberJpaMapperImpl implements MemberJpaMapper {

    @Override
    public SiteMemberEntity toMemberEntity(Member member) {
        SiteMemberEntity.SiteMemberEntityBuilder builder = SiteMemberEntity.builder().nickname(member.getMemberNickname().getValue());
        if (member.getMemberId() != null) {
            builder.uuid(member.getMemberId().getValue());
        }
        if (member.getMemberStatus() != null) {
            builder.isActive(member.getMemberStatus().isActive());
        }
        if (member.getMemberBirthDate() != null) {
            builder.birthDate(member.getMemberBirthDate().getValue());
        }
        return builder.build();
    }

    @Override
    public Member toMember(SiteMemberEntity entity) {
        MemberStatus status;
        if (entity.getIsActive()) {
            status = MemberStatus.active();
        } else {
            status = MemberStatus.inactive();
        }
        return Member.create(MemberId.fromUuid(entity.getUuid()), status, MemberNickname.create(entity.getNickname()), MemberBirthDate.create(entity.getBirthDate()));
    }
}
