package kr.modusplant.domains.member.framework.outbound.jpa.mapper.supers;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberProfileEntity;

import java.io.IOException;

public interface MemberProfileJpaMapper {
    MemberProfileEntity toMemberProfileEntity(MemberProfile memberProfile);

    MemberProfile toMemberProfile(MemberProfileEntity entity) throws IOException;
}
