package kr.modusplant.legacy.domains.term.mapper;

import kr.modusplant.framework.out.jpa.entity.TermEntity;
import kr.modusplant.legacy.domains.term.app.http.request.TermInsertRequest;
import kr.modusplant.legacy.domains.term.app.http.response.TermResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TermAppInfraMapper {
    @Mapping(target = "termEntity", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    TermEntity toTermEntity(TermInsertRequest termInsertRequest);

    TermResponse toTermResponse(TermEntity termEntity);
}
