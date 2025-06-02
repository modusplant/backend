package kr.modusplant.domains.member.mapper;

import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.*;

@Mapper
public interface SiteMemberAuthDomainInfraMapper {
    @Mapping(source = ACTIVE_MEMBER, target = ACTIVE_MEMBER_UUID, qualifiedByName = "toActiveMemberUuid")
    @Mapping(source = ORIGINAL_MEMBER, target = ORIGINAL_MEMBER_UUID, qualifiedByName = "toOriginalMemberUuid")
    @Mapping(target = MEMBER_AUTH, ignore = true)
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
