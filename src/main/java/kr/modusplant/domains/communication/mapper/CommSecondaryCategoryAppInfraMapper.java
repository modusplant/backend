package kr.modusplant.domains.communication.mapper;

import kr.modusplant.domains.communication.app.http.request.CommCategoryInsertRequest;
import kr.modusplant.domains.communication.app.http.response.CommCategoryResponse;
import kr.modusplant.domains.communication.persistence.entity.CommSecondaryCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CommSecondaryCategoryAppInfraMapper {
    @Mapping(target = "commCategoryEntity", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    CommSecondaryCategoryEntity toCommCategoryEntity(CommCategoryInsertRequest commCategoryInsertRequest);

    CommCategoryResponse toCommCategoryResponse(CommSecondaryCategoryEntity commSecondaryCategoryEntity);
}
