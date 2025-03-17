package kr.modusplant.global.mapper;

import kr.modusplant.global.domain.model.SiteMemberRole;
import kr.modusplant.global.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public interface SiteMemberRoleEntityMapper {
    @BeanMapping
    default SiteMemberRoleEntity createSiteMemberRoleEntity(SiteMemberRole memberRole) {
        return SiteMemberRoleEntity.builder().role(memberRole.getRole()).build();
    }

    @BeanMapping
    default SiteMemberRoleEntity updateSiteMemberRoleEntity(SiteMemberRole memberRole, @Context SiteMemberJpaRepository memberRepository) {
        return SiteMemberRoleEntity.builder().member(memberRepository.findByUuid(memberRole.getUuid()).orElseThrow()).role(memberRole.getRole()).build();
    }

    SiteMemberRole toSiteMemberRole(SiteMemberRoleEntity memberRoleEntity);
}
