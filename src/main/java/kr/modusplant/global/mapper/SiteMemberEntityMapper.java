package kr.modusplant.global.mapper;

import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

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

    SiteMember toSiteMember(SiteMemberEntity siteMemberEntity);
}
