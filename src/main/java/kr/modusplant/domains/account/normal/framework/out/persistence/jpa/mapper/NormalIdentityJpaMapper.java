package kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.kernel.Nickname;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NormalIdentityJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "nickname", source = "nickname.value")
    SiteMemberEntity toSiteMemberEntity(Nickname nickname);
}
