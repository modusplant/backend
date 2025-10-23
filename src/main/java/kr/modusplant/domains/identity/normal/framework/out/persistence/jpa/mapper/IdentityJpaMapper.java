package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IdentityJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "nickname", source = "nickname.nickname")
    SiteMemberEntity toSiteMemberEntity(SignUpData sign);
}
