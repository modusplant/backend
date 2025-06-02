package kr.modusplant.domains.communication.conversation.mapper;

import kr.modusplant.domains.communication.conversation.app.http.request.ConvCategoryInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCategoryResponse;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ConvCategoryAppInfraMapper {
    @Mapping(target = "convCategoryEntity", ignore = true)
    ConvCategoryEntity toConvCategoryEntity(ConvCategoryInsertRequest convCategoryInsertRequest);

    ConvCategoryResponse toConvCategoryResponse(ConvCategoryEntity convCategoryEntity);
}
