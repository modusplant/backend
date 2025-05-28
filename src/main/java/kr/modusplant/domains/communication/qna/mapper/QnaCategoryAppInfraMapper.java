package kr.modusplant.domains.communication.qna.mapper;

import kr.modusplant.domains.communication.qna.app.http.request.QnaCategoryInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaCategoryResponse;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface QnaCategoryAppInfraMapper {
    @Mapping(target = "qnaCategoryEntity", ignore = true)
    QnaCategoryEntity toQnaCategoryEntity(QnaCategoryInsertRequest qnaCategoryInsertRequest);

    QnaCategoryResponse toQnaCategoryResponse(QnaCategoryEntity qnaCategoryEntity);
}
