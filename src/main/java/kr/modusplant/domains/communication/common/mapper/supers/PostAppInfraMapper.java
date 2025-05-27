package kr.modusplant.domains.communication.common.mapper.supers;

import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.mapstruct.Named;

public interface PostAppInfraMapper {

    @Named("toGroupOrder")
    default Integer toGroupOrder(PlantGroupEntity plantGroupEntity) {
        return plantGroupEntity.getOrder();
    }

    @Named("toCategory")
    default String toCategory(PlantGroupEntity plantGroupEntity) {
        return plantGroupEntity.getCategory();
    }

    @Named("toNickname")
    default String toNickname(SiteMemberEntity siteMemberEntity) {
        return siteMemberEntity.getNickname();
    }
}
