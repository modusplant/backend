package kr.modusplant.global.middleware.security.mapper;

import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface SiteMemberAuthEntityToDomainMapper {

    @Mapping(source = "activeMember", target = "activeMemberUuid", qualifiedByName = "getUuid")
    @Mapping(target = "originalMemberUuid", ignore = true)
    @Mapping(target = "providerId", ignore = true)
    @Mapping(target = "failedAttempt", ignore = true)
    @Mapping(target = "lockoutRefreshAt", ignore = true)
    @Mapping(target = "lockoutUntil", ignore = true)
    @Mapping(target = "memberAuth", ignore = true)
    SiteMemberAuth toSiteMemberAuth(SiteMemberAuthEntity memberAuthEntity);

    @Named("getUuid")
    default UUID getUuid(SiteMemberEntity memberEntity) {
        return memberEntity != null ? memberEntity.getUuid() : null;
    }
}
