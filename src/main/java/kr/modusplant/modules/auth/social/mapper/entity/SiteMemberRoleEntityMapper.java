package kr.modusplant.modules.auth.social.mapper.entity;

import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.enums.Role;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper
public interface SiteMemberRoleEntityMapper {
    @BeanMapping
    default SiteMemberRoleEntity toSiteMemberRoleEntity(UUID memberUuid, @Context SiteMemberRepository memberRepository) {
        return SiteMemberRoleEntity.builder()
                .member(memberRepository.findByUuid(memberUuid).orElseThrow())
                .role(Role.ROLE_USER).build();
    }
}
