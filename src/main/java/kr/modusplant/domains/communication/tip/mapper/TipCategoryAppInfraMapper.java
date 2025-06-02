package kr.modusplant.domains.communication.tip.mapper;

import kr.modusplant.domains.communication.tip.app.http.request.TipCategoryInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipCategoryResponse;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TipCategoryAppInfraMapper {
    @Mapping(target = "tipCategoryEntity", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    TipCategoryEntity toTipCategoryEntity(TipCategoryInsertRequest tipCategoryInsertRequest);

    TipCategoryResponse toTipCategoryResponse(TipCategoryEntity tipCategoryEntity);
}
