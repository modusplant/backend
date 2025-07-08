package kr.modusplant.domains.communication.mapper;

import kr.modusplant.domains.communication.app.http.request.CommCategoryInsertRequest;
import kr.modusplant.domains.communication.app.http.response.CommCategoryResponse;
import kr.modusplant.domains.communication.persistence.entity.CommPrimaryCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CommPrimaryCategoryAppInfraMapper {
    @Mapping(target = "commCategoryEntity", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    CommPrimaryCategoryEntity toCommCategoryEntity(CommCategoryInsertRequest commCategoryInsertRequest);

    CommCategoryResponse toCommCategoryResponse(CommPrimaryCategoryEntity commPrimaryCategoryEntity);
}
