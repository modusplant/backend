package kr.modusplant.api.crud.member.mapper;

import kr.modusplant.api.crud.member.domain.model.SiteMemberRole;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberCrudJpaRepository;
import org.mapstruct.*;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER;
import static kr.modusplant.global.vo.CamelCaseWord.MEMBER_ROLE;

@Mapper
public interface SiteMemberRoleEntityMapper {
    @BeanMapping
    default SiteMemberRoleEntity createSiteMemberRoleEntity(SiteMemberRole memberRole, @Context SiteMemberCrudJpaRepository memberRepository) {
        return SiteMemberRoleEntity.builder()
                .member(memberRepository.findByUuid(memberRole.getUuid()).orElseThrow())
                .role(memberRole.getRole()).build();
    }

    @BeanMapping
    default SiteMemberRoleEntity updateSiteMemberRoleEntity(SiteMemberRole memberRole, @Context SiteMemberCrudJpaRepository memberRepository) {
        return SiteMemberRoleEntity.builder()
                .member(memberRepository.findByUuid(memberRole.getUuid()).orElseThrow())
                .role(memberRole.getRole()).build();
    }

    @Mapping(source = MEMBER, target = "uuid", qualifiedByName = "toUuid")
    @Mapping(target = MEMBER_ROLE, ignore = true)
    SiteMemberRole toSiteMemberRole(SiteMemberRoleEntity memberRoleEntity);

    @Named("toUuid")
    default UUID toUuid(SiteMemberEntity member) {
        return member.getUuid();
    }
}
