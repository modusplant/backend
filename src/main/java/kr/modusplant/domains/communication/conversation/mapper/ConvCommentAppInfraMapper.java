package kr.modusplant.domains.communication.conversation.mapper;

import kr.modusplant.domains.communication.common.mapper.supers.PostAppInfraMapper;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCommentResponse;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface ConvCommentAppInfraMapper extends PostAppInfraMapper {

    @Mapping(source = "postEntity", target = "postUlid", qualifiedByName = "toPostUlid")
    @Mapping(source = "authMember", target = "memberUuid", qualifiedByName = "toMemberUuid")
    @Mapping(source = "authMember", target = "nickname", qualifiedByName = "toNickname")
    ConvCommentResponse toConvCommentResponse(ConvCommentEntity convCommentEntity);

    @Mapping(target = "ConvCommentEntity", ignore = true)
    @Mapping(source = "createMemberUuid", target = "authMember", qualifiedByName = "toMemberEntity")
    @Mapping(source = "createMemberUuid", target = "createMember", qualifiedByName = "toMemberEntity")
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(source = "postUlid", target = "postEntity", qualifiedByName = "toConvPostEntity")
    ConvCommentEntity toConvCommentEntity(ConvCommentInsertRequest insertRequest,
                                          @Context ConvPostRepository convPostRepository, @Context SiteMemberRepository memberRepository);

    @Named("toPostUlid")
    default String toPostUlid(ConvPostEntity postEntity) {
        return postEntity.getUlid();
    }

    @Named("toMemberEntity")
    default SiteMemberEntity toMemberEntity(UUID memberUuid, @Context SiteMemberRepository memberRepository) {
        return memberRepository.findByUuid(memberUuid).orElseThrow();
    }

    @Named("toConvPostEntity")
    default ConvPostEntity toConvPostEntity(String ulid, @Context ConvPostRepository convPostRepository) {
        return convPostRepository.findByUlid(ulid).orElseThrow();
    }
}
