<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/mapper/SiteMemberEntityMapper.java
package kr.modusplant.domains.member.mapper;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
========
package kr.modusplant.api.crud.member.mapper;

import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/main/java/kr/modusplant/api/crud/member/mapper/SiteMemberEntityMapper.java
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER;

@Mapper
public interface SiteMemberEntityMapper {
    @BeanMapping(ignoreByDefault = true)
    default SiteMemberEntity createSiteMemberEntity(SiteMember member) {
        return SiteMemberEntity.builder()
                .nickname(member.getNickname())
                .birthDate(member.getBirthDate())
                .isActive(member.getIsActive())
                .isDisabledByLinking(member.getIsDisabledByLinking())
                .isBanned(member.getIsBanned())
                .isDeleted(member.getIsDeleted())
                .loggedInAt(member.getLoggedInAt()).build();
    }

    @BeanMapping(ignoreByDefault = true)
    default SiteMemberEntity updateSiteMemberEntity(SiteMember member) {
        return SiteMemberEntity.builder()
                .uuid(member.getUuid())
                .nickname(member.getNickname())
                .birthDate(member.getBirthDate())
                .isActive(member.getIsActive())
                .isDisabledByLinking(member.getIsDisabledByLinking())
                .isBanned(member.getIsBanned())
                .isDeleted(member.getIsDeleted())
                .loggedInAt(member.getLoggedInAt()).build();
    }

    @Mapping(target = MEMBER, ignore = true)
    SiteMember toSiteMember(SiteMemberEntity siteMemberEntity);
}
