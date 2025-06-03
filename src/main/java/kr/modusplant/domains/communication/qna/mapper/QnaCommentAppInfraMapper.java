package kr.modusplant.domains.communication.qna.mapper;

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
public interface QnaCommentAppInfraMapper {

    @Mapping(source = "postEntity", target = "postUlid", qualifiedByName = "getPostUlid")
    @Mapping(source = "authMember", target = "authMemberUuid", qualifiedByName = "getMemberUuid")
    @Mapping(source = "createMember", target = "createMemberUuid", qualifiedByName = "getMemberUuid")
    QnaCommentResponse toQnaCommentResponse(QnaCommentEntity qnaCommentEntity);

    @Mapping(target = "QnaCommentEntity", ignore = true)
    @Mapping(source = "createMemberUuid", target = "authMember", qualifiedByName = "toMemberEntity")
    @Mapping(source = "createMemberUuid", target = "createMember", qualifiedByName = "toMemberEntity")
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(source = "postUlid", target = "postEntity", qualifiedByName = "toQnaPostEntity")
    QnaCommentEntity toQnaCommentEntity(QnaCommentInsertRequest insertRequest,
                                        @Context QnaPostRepository qnaPostRepository, @Context SiteMemberRepository memberRepository);

    @Named("getPostUlid")
    default String getPostUlid(QnaPostEntity postEntity) {
        return postEntity.getUlid();
    }

    @Named("getMemberUuid")
    default UUID getMemberUuid(SiteMemberEntity member) {
        return member.getUuid();
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
