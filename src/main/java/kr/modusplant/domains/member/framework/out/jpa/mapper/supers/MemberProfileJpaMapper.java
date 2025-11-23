package kr.modusplant.domains.member.framework.out.jpa.mapper.supers;

import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity;

import java.io.IOException;

public interface MemberProfileJpaMapper {
    SiteMemberProfileEntity toMemberProfileEntity(MemberProfile memberProfile);

    MemberProfile toMemberProfile(SiteMemberProfileEntity entity) throws IOException;
}
