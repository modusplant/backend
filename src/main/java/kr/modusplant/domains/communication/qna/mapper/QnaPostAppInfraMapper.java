package kr.modusplant.domains.communication.qna.mapper;

import kr.modusplant.domains.communication.common.mapper.supers.PostAppInfraMapper;
import kr.modusplant.domains.communication.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static kr.modusplant.domains.member.vo.MemberUuid.MEMBER_UUID;
import static kr.modusplant.global.vo.CamelCaseWord.CATEGORY;
import static kr.modusplant.global.vo.EntityFieldName.AUTH_MEMBER;
import static kr.modusplant.global.vo.EntityFieldName.NICKNAME;

@Mapper
public interface QnaPostAppInfraMapper extends PostAppInfraMapper {

    @Mapping(source = CATEGORY, target = CATEGORY, qualifiedByName = "toCategory")
    @Mapping(source = CATEGORY, target = "categoryUuid", qualifiedByName = "toCategoryUuid")
    @Mapping(source = CATEGORY, target = "categoryOrder", qualifiedByName = "toCategoryOrder")
    @Mapping(source = AUTH_MEMBER, target = MEMBER_UUID, qualifiedByName = "toMemberUuid")
    @Mapping(source = AUTH_MEMBER, target = NICKNAME, qualifiedByName = "toNickname")
    QnaPostResponse toQnaPostResponse(QnaPostEntity qnaPostEntity);

    @Named("toCategory")
    default String toCategory(QnaCategoryEntity qnaCategoryEntity) {
        return qnaCategoryEntity.getCategory();
    }

    @Named("toCategoryUuid")
    default UUID toCategoryUuid(QnaCategoryEntity qnaCategoryEntity) {
        return qnaCategoryEntity.getUuid();
    }

    @Named("toCategoryOrder")
    default Integer toCategoryOrder(QnaCategoryEntity qnaCategoryEntity) {
        return qnaCategoryEntity.getOrder();
    }
}
