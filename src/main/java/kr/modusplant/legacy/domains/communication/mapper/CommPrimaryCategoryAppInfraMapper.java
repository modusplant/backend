package kr.modusplant.legacy.domains.communication.mapper;

import kr.modusplant.legacy.domains.communication.app.http.request.CommCategoryInsertRequest;
import kr.modusplant.legacy.domains.communication.app.http.response.CommCategoryResponse;
import kr.modusplant.framework.out.persistence.entity.CommPrimaryCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CommPrimaryCategoryAppInfraMapper {
    @Mapping(target = "commCategoryEntity", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    CommPrimaryCategoryEntity toCommCategoryEntity(CommCategoryInsertRequest commCategoryInsertRequest);

    CommCategoryResponse toCommCategoryResponse(CommPrimaryCategoryEntity commPrimaryCategoryEntity);
}
