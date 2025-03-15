package kr.modusplant.global.mapper;

import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import static kr.modusplant.global.util.MapperUtils.map;

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
    default SiteMemberEntity updateSiteMemberEntity(SiteMember member,
                                                    @Context SiteMemberJpaRepository siteMemberRepository) {
        return map(member, siteMemberRepository.findByUuid(member.getUuid())
                .orElseThrow(() -> new EntityNotFoundWithUuidException(member.getUuid(), SiteMemberEntity.class)));
    }

    SiteMember toSiteMember(SiteMemberEntity siteMemberEntity);
}
