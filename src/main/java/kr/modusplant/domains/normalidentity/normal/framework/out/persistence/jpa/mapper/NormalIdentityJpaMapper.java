package kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.normalidentity.normal.domain.vo.Nickname;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NormalIdentityJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "nickname", source = "memberNickname.nickname")
    SiteMemberEntity toSiteMemberEntity(Nickname memberNickname);
}
