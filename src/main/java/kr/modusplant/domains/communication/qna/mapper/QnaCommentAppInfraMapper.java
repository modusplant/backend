package kr.modusplant.domains.communication.qna.mapper;

import kr.modusplant.domains.communication.common.mapper.supers.PostAppInfraMapper;
import kr.modusplant.domains.communication.qna.app.http.request.QnaCommentInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaCommentResponse;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface QnaCommentAppInfraMapper extends PostAppInfraMapper {

    @Mapping(source = "postEntity", target = "postUlid", qualifiedByName = "toPostUlid")
    @Mapping(source = "authMember", target = "memberUuid", qualifiedByName = "toMemberUuid")
    @Mapping(source = "authMember", target = "nickname", qualifiedByName = "toNickname")
    QnaCommentResponse toQnaCommentResponse(QnaCommentEntity qnaCommentEntity);

    @Mapping(target = "QnaCommentEntity", ignore = true)
    @Mapping(source = "createMemberUuid", target = "authMember", qualifiedByName = "toMemberEntity")
    @Mapping(source = "createMemberUuid", target = "createMember", qualifiedByName = "toMemberEntity")
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(source = "postUlid", target = "postEntity", qualifiedByName = "toQnaPostEntity")
    QnaCommentEntity toQnaCommentEntity(QnaCommentInsertRequest insertRequest,
                                        @Context QnaPostRepository qnaPostRepository, @Context SiteMemberRepository memberRepository);

    @Named("toPostUlid")
    default String toPostUlid(QnaPostEntity postEntity) {
        return postEntity.getUlid();
    }

    @Named("toMemberEntity")
    default SiteMemberEntity toMemberEntity(UUID memberUuid, @Context SiteMemberRepository memberRepository) {
        return memberRepository.findByUuid(memberUuid).orElseThrow();
    }

    @Named("toQnaPostEntity")
    default QnaPostEntity toQnaPostEntity(String ulid, @Context QnaPostRepository qnaPostRepository) {
        return qnaPostRepository.findByUlid(ulid).orElseThrow();
    }
}
