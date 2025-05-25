package kr.modusplant.domains.tip.mapper;

import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.*;

@Mapper
public interface TipPostAppInfraMapper {

    @Mapping(source = GROUP, target = GROUP_ORDER, qualifiedByName = "toGroupOrder")
    @Mapping(source = GROUP, target = CATEGORY, qualifiedByName = "toCategory")
    @Mapping(source = AUTH_MEMBER, target = NICKNAME, qualifiedByName = "toNickname")
    TipPostResponse toTipPostResponse(TipPostEntity tipPostEntity);

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
