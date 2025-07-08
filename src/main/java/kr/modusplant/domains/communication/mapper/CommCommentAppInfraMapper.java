package kr.modusplant.domains.communication.mapper;

import kr.modusplant.domains.communication.app.http.response.CommCommentResponse;
import kr.modusplant.domains.communication.persistence.entity.CommCommentEntity;
import kr.modusplant.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface CommCommentAppInfraMapper {

    @Mapping(source = "postEntity", target = "postUlid", qualifiedByName = "toPostUlid")
    @Mapping(source = "authMember", target = "memberUuid", qualifiedByName = "toMemberUuid")
    @Mapping(source = "authMember", target = "nickname", qualifiedByName = "toNickname")
    CommCommentResponse toCommCommentResponse(CommCommentEntity commCommentEntity);

    @Named("toPostUlid")
    default String toPostUlid(CommPostEntity postEntity) {
        return postEntity.getUlid();
    }

    @Named("toMemberEntity")
    default SiteMemberEntity toMemberEntity(UUID memberUuid, @Context SiteMemberRepository memberRepository) {
        return memberRepository.findByUuid(memberUuid).orElseThrow();
    }

    @Named("toCommPostEntity")
    default CommPostEntity toCommPostEntity(String ulid, @Context CommPostRepository commPostRepository) {
        return commPostRepository.findByUlid(ulid).orElseThrow();
    }

    @Named("toMemberUuid")
    default UUID toMemberUuid(SiteMemberEntity member) {
        return member.getUuid();
    }

    @Named("toNickname")
    default String toNickname(SiteMemberEntity siteMemberEntity) {
        return siteMemberEntity.getNickname();
    }
}
