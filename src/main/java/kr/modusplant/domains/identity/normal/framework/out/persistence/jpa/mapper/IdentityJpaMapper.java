package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.identity.normal.domain.vo.Nickname;
import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface IdentityJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "nickname", source = "nickname", qualifiedByName = "mapNickname")
    SiteMemberEntity toSiteMemberEntity(SignUpData sign);

    @Named("mapNickname")
    default String mapNickname(Nickname nickname) { return nickname.getNickname(); }
}
