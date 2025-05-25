package kr.modusplant.domains.qna.mapper;

import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.qna.persistence.entity.QnaPostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static kr.modusplant.global.vo.CamelCaseWord.*;

@Mapper
public interface QnaPostAppInfraMapper {

    @Mapping(source = GROUP, target = GROUP_ORDER, qualifiedByName = "toGroupOrder")
    @Mapping(source = GROUP, target = CATEGORY, qualifiedByName = "toCategory")
    @Mapping(source = AUTH_MEMBER, target = NICKNAME, qualifiedByName = "toNickname")
    QnaPostResponse toQnaPostResponse(QnaPostEntity qnaPostEntity);

    @Named("toGroupOrder")
    default Integer toGroupOrder(PlantGroupEntity plantGroupEntity) {
        return plantGroupEntity.getOrder();
    }

    @Named("toCategory")
    default String toCategory(PlantGroupEntity plantGroupEntity) {
        return plantGroupEntity.getCategory();
    }

    @Named("toNickname")
    default String toNickname(SiteMemberEntity siteMemberEntity) {
        return siteMemberEntity.getNickname();
    }

}
