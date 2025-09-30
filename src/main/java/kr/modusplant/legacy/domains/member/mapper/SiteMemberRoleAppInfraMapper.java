package kr.modusplant.legacy.domains.member.mapper;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberRoleInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberRoleResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface SiteMemberRoleAppInfraMapper {

    @Mapping(target = "memberRoleEntity", ignore = true)
    @Mapping(source = "uuid", target = "member", qualifiedByName = "toMember")
    SiteMemberRoleEntity toMemberRoleEntity(SiteMemberRoleInsertRequest memberRoleInsertRequest, @Context SiteMemberJpaRepository memberRepository);

    @Mapping(source = "member", target = "uuid", qualifiedByName = "toUuid")
    SiteMemberRoleResponse toMemberRoleResponse(SiteMemberRoleEntity memberRoleEntity);

    @Named("toMember")
    default SiteMemberEntity toMember(UUID uuid, @Context SiteMemberJpaRepository memberRepository) {
        return memberRepository.findByUuid(uuid).orElseThrow();
    }

    @Named("toUuid")
    default UUID toUuid(SiteMemberEntity memberEntity) {
        return memberEntity.getUuid();
    }
}
