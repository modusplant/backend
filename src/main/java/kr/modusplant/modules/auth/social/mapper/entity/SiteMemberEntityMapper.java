package kr.modusplant.modules.auth.social.mapper.entity;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;


@Mapper
public interface SiteMemberEntityMapper {
    @BeanMapping(ignoreByDefault = true)
    default SiteMemberEntity toSiteMemberEntity(String nickname) {
        return SiteMemberEntity.builder()
                .nickname(nickname)
                .loggedInAt(LocalDateTime.now())
                .build();
    }
}
