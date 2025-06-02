package kr.modusplant.domains.communication.conversation.mapper;

import kr.modusplant.domains.communication.common.mapper.supers.PostAppInfraMapper;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvPostResponse;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static kr.modusplant.global.vo.CamelCaseWord.*;

@Mapper
public interface ConvPostAppInfraMapper extends PostAppInfraMapper {

    @Mapping(source = GROUP, target = GROUP_ORDER, qualifiedByName = "toGroupOrder")
    @Mapping(source = GROUP, target = CATEGORY, qualifiedByName = "toCategory")
    @Mapping(source = AUTH_MEMBER, target = NICKNAME, qualifiedByName = "toNickname")
    ConvPostResponse toConvPostResponse(ConvPostEntity convPostEntity);

    @Named("toGroupOrder")
    default Integer toGroupOrder(ConvCategoryEntity convCategoryEntity) {
        return convCategoryEntity.getOrder();
    }

    @Named("toCategory")
    default String toCategory(ConvCategoryEntity convCategoryEntity) {
        return convCategoryEntity.getCategory();
    }
}
