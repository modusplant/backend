<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/mapper/SiteMemberRoleEntityMapper.java
package kr.modusplant.domains.member.mapper;

import kr.modusplant.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberCrudJpaRepository;
========
package kr.modusplant.api.crud.member.mapper;

import kr.modusplant.api.crud.member.domain.model.SiteMemberRole;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberJpaRepository;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/main/java/kr/modusplant/api/crud/member/mapper/SiteMemberRoleEntityMapper.java
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
