package kr.modusplant.domains.member.mapper;

import kr.modusplant.domains.member.app.http.request.SiteMemberRoleInsertRequest;
import kr.modusplant.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER;
import static kr.modusplant.global.vo.CamelCaseWord.MEMBER_ROLE_ENTITY;

@Mapper
public interface SiteMemberRoleAppInfraMapper {

    @Mapping(target = MEMBER_ROLE_ENTITY, ignore = true)
    @Mapping(source = "uuid", target = MEMBER, qualifiedByName = "toMember")
    SiteMemberRoleEntity toMemberRoleEntity(SiteMemberRoleInsertRequest memberRoleInsertRequest, @Context SiteMemberRepository memberRepository);

    @Mapping(source = MEMBER, target = "uuid", qualifiedByName = "toUuid")
    SiteMemberRoleResponse toMemberRoleResponse(SiteMemberRoleEntity memberRoleEntity);

    @Named("toMember")
    default SiteMemberEntity toMember(UUID uuid, @Context SiteMemberRepository memberRepository) {
        return memberRepository.findByUuid(uuid).orElseThrow();
    }

    @Named("toUuid")
    default UUID toUuid(SiteMemberEntity memberEntity) {
        return memberEntity.getUuid();
    }
}
