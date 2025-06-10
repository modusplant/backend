package kr.modusplant.domains.member.mapper;

import kr.modusplant.domains.member.app.http.request.SiteMemberTermInsertRequest;
import kr.modusplant.domains.member.app.http.response.SiteMemberTermResponse;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER;

@Mapper
public interface SiteMemberTermAppInfraMapper {

    @Mapping(target = "memberTermEntity", ignore = true)
    @Mapping(source = "uuid", target = MEMBER, qualifiedByName = "toMember")
    SiteMemberTermEntity toMemberTermEntity(SiteMemberTermInsertRequest memberTermInsertRequest, @Context SiteMemberRepository memberRepository);

    @Mapping(source = MEMBER, target = "uuid", qualifiedByName = "toUuid")
    SiteMemberTermResponse toMemberTermResponse(SiteMemberTermEntity memberTermEntity);

    @Named("toMember")
    default SiteMemberEntity toMember(UUID uuid, @Context SiteMemberRepository memberRepository) {
        return memberRepository.findByUuid(uuid).orElseThrow();
    }

    @Named("toUuid")
    default UUID toUuid(SiteMemberEntity memberEntity) {
        return memberEntity.getUuid();
    }
}
