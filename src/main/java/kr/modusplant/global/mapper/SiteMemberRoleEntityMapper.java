package kr.modusplant.global.mapper;

import kr.modusplant.global.domain.model.SiteMemberRole;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import org.mapstruct.*;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER;

@Mapper
public interface SiteMemberRoleEntityMapper {
    @BeanMapping
    default SiteMemberRoleEntity createSiteMemberRoleEntity(SiteMemberRole memberRole, @Context SiteMemberJpaRepository memberRepository) {
        return SiteMemberRoleEntity.builder()
                .member(memberRepository.findByUuid(memberRole.getUuid()).orElseThrow())
                .role(memberRole.getRole()).build();
    }

    @BeanMapping
    default SiteMemberRoleEntity updateSiteMemberRoleEntity(SiteMemberRole memberRole, @Context SiteMemberJpaRepository memberRepository) {
        return SiteMemberRoleEntity.builder()
                .member(memberRepository.findByUuid(memberRole.getUuid()).orElseThrow())
                .role(memberRole.getRole()).build();
    }

    @Mapping(source = MEMBER, target = "uuid", qualifiedByName = "toUuid")
    SiteMemberRole toSiteMemberRole(SiteMemberRoleEntity memberRoleEntity);

    @Named("toUuid")
    default UUID toUuid(SiteMemberEntity member) {
        return member.getUuid();
    }
}
