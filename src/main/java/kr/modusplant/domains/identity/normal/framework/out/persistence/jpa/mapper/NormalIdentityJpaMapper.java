package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.identity.normal.domain.vo.NormalNickname;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NormalIdentityJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "nickname", source = "nickname.value")
    SiteMemberEntity toSiteMemberEntity(NormalNickname nickname);
}
