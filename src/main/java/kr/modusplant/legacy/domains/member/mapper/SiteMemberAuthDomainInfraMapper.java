package kr.modusplant.legacy.domains.member.mapper;

import kr.modusplant.framework.out.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.legacy.domains.member.domain.model.SiteMemberAuth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static kr.modusplant.infrastructure.persistence.constant.EntityFieldName.ACTIVE_MEMBER;
import static kr.modusplant.infrastructure.persistence.constant.EntityFieldName.ORIGINAL_MEMBER;

@Mapper
public interface SiteMemberAuthDomainInfraMapper {
    @Mapping(source = ACTIVE_MEMBER, target = "activeMemberUuid", qualifiedByName = "toActiveMemberUuid")
    @Mapping(source = ORIGINAL_MEMBER, target = "originalMemberUuid", qualifiedByName = "toOriginalMemberUuid")
    @Mapping(target = "memberAuth", ignore = true)
    SiteMemberAuth toSiteMemberAuth(SiteMemberAuthEntity memberAuthEntity);

    @Named("toActiveMemberUuid")
    default UUID toActiveMemberUuid(SiteMemberEntity activeMemberEntity) {
        return activeMemberEntity.getUuid();
    }

    @Named("toOriginalMemberUuid")
    default UUID toOriginalMemberUuid(SiteMemberEntity originalMemberEntity) {
        return originalMemberEntity.getUuid();
    }
}
