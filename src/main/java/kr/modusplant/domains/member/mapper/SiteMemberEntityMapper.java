package kr.modusplant.domains.member.mapper;

import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
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
