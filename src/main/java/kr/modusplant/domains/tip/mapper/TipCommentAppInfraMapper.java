package kr.modusplant.domains.tip.mapper;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.tip.persistence.repository.TipPostRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface TipCommentAppInfraMapper {

    @Mapping(source = "postEntity", target = "postUlid", qualifiedByName = "getPostUlid")
    @Mapping(source = "authMember", target = "authMemberUuid", qualifiedByName = "getMemberUuid")
    @Mapping(source = "createMember", target = "createMemberUuid", qualifiedByName = "getMemberUuid")
    TipCommentResponse toTipCommentResponse(TipCommentEntity tipCommentEntity);

    @Mapping(target = "TipCommentEntity", ignore = true)
    @Mapping(source = "createMemberUuid", target = "authMember", qualifiedByName = "toMemberEntity")
    @Mapping(source = "createMemberUuid", target = "createMember", qualifiedByName = "toMemberEntity")
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(source = "postUlid", target = "postEntity", qualifiedByName = "toTipPostEntity")
    TipCommentEntity toTipCommentEntity(TipCommentInsertRequest insertRequest,
                                        @Context TipPostRepository tipPostRepository, @Context SiteMemberRepository memberRepository);

    @Named("getPostUlid")
    default String getPostUlid(TipPostEntity postEntity) {
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

    @Named("toTipPostEntity")
    default TipPostEntity toTipPostEntity(String ulid, @Context TipPostRepository tipPostRepository) {
        return tipPostRepository.findByUlid(ulid).orElseThrow();
    }
}
