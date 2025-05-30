package kr.modusplant.domains.communication.tip.mapper;

import kr.modusplant.domains.communication.common.mapper.supers.PostAppInfraMapper;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.app.http.response.TipPostResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static kr.modusplant.global.vo.CamelCaseWord.*;

@Mapper
public interface TipPostAppInfraMapper extends PostAppInfraMapper {

    @Mapping(source = GROUP, target = GROUP_ORDER, qualifiedByName = "toGroupOrder")
    @Mapping(source = GROUP, target = CATEGORY, qualifiedByName = "toCategory")
    @Mapping(source = AUTH_MEMBER, target = NICKNAME, qualifiedByName = "toNickname")
    TipPostResponse toTipPostResponse(TipPostEntity tipPostEntity);

    @Named("toGroupOrder")
    default Integer toGroupOrder(TipCategoryEntity tipCategoryEntity) {
        return tipCategoryEntity.getOrder();
    }

    @Named("toCategory")
    default String toCategory(TipCategoryEntity tipCategoryEntity) {
        return tipCategoryEntity.getCategory();
    }

}
