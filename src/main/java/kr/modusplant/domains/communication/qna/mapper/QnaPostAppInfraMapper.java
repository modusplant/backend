package kr.modusplant.domains.communication.qna.mapper;

import kr.modusplant.domains.communication.common.mapper.supers.PostAppInfraMapper;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static kr.modusplant.global.vo.CamelCaseWord.*;

@Mapper
public interface QnaPostAppInfraMapper extends PostAppInfraMapper {

    @Mapping(source = GROUP, target = GROUP_ORDER, qualifiedByName = "toGroupOrder")
    @Mapping(source = GROUP, target = CATEGORY, qualifiedByName = "toCategory")
    @Mapping(source = AUTH_MEMBER, target = NICKNAME, qualifiedByName = "toNickname")
    QnaPostResponse toQnaPostResponse(QnaPostEntity qnaPostEntity);

    @Named("toGroupOrder")
    default Integer toGroupOrder(QnaCategoryEntity qnaCategoryEntity) {
        return qnaCategoryEntity.getOrder();
    }

    @Named("toCategory")
    default String toCategory(QnaCategoryEntity qnaCategoryEntity) {
        return qnaCategoryEntity.getCategory();
    }

}
