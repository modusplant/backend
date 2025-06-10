package kr.modusplant.domains.communication.tip.mapper;

import kr.modusplant.domains.communication.common.mapper.supers.PostAppInfraMapper;
import kr.modusplant.domains.communication.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface TipCommentAppInfraMapper extends PostAppInfraMapper {

    @Mapping(source = "postEntity", target = "postUlid", qualifiedByName = "toPostUlid")
    @Mapping(source = "authMember", target = "memberUuid", qualifiedByName = "toMemberUuid")
    @Mapping(source = "authMember", target = "nickname", qualifiedByName = "toNickname")
    TipCommentResponse toTipCommentResponse(TipCommentEntity tipCommentEntity);

    @Mapping(target = "TipCommentEntity", ignore = true)
    @Mapping(source = "createMemberUuid", target = "authMember", qualifiedByName = "toMemberEntity")
    @Mapping(source = "createMemberUuid", target = "createMember", qualifiedByName = "toMemberEntity")
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(source = "postUlid", target = "postEntity", qualifiedByName = "toTipPostEntity")
    TipCommentEntity toTipCommentEntity(TipCommentInsertRequest insertRequest,
                                        @Context TipPostRepository tipPostRepository, @Context SiteMemberRepository memberRepository);

    @Named("toPostUlid")
    default String toPostUlid(TipPostEntity postEntity) {
        return postEntity.getUlid();
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
