package kr.modusplant.domains.member.framework.out.jpa.mapper;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberStatus;
import kr.modusplant.domains.member.framework.out.jpa.mapper.supers.MemberJpaMapper;
import kr.modusplant.framework.jpa.entity.MemberEntity;
import kr.modusplant.shared.kernel.Nickname;
import org.springframework.stereotype.Component;

@Component
public class MemberJpaMapperImpl implements MemberJpaMapper {

    @Override
    public MemberEntity toMemberEntity(Nickname nickname) {
        return MemberEntity.builder().nickname(nickname.getValue()).build();
    }

    @Override
    public MemberEntity toMemberEntity(MemberId memberId, Nickname nickname) {
        return MemberEntity.builder().uuid(memberId.getValue()).nickname(nickname.getValue()).build();
    }

    @Override
    public Member toMember(MemberEntity entity) {
        MemberStatus status;
        if (entity.getIsActive()) {
            status = MemberStatus.active();
        } else {
            status = MemberStatus.inactive();
        }
        return Member.create(
                MemberId.fromUuid(entity.getUuid()),
                status,
                Nickname.create(entity.getNickname())
        );
    }
}
