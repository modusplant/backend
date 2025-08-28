package kr.modusplant.legacy.domains.term.mapper;

import kr.modusplant.legacy.domains.term.app.http.request.TermInsertRequest;
import kr.modusplant.legacy.domains.term.app.http.response.TermResponse;
import kr.modusplant.framework.out.persistence.entity.TermEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TermAppInfraMapper {
    @Mapping(target = "termEntity", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    TermEntity toTermEntity(TermInsertRequest termInsertRequest);

    TermResponse toTermResponse(TermEntity termEntity);
}
