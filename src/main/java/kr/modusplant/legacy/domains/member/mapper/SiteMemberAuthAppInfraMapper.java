package kr.modusplant.legacy.domains.member.mapper;

import kr.modusplant.framework.out.persistence.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.out.persistence.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.persistence.jpa.repository.SiteMemberRepository;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberAuthInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberAuthResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

import static kr.modusplant.framework.out.persistence.constant.EntityFieldName.*;
import static kr.modusplant.legacy.domains.member.vo.MemberUuid.ACTIVE_MEMBER_UUID;
import static kr.modusplant.legacy.domains.member.vo.MemberUuid.ORIGINAL_MEMBER_UUID;

@Mapper
public interface SiteMemberAuthAppInfraMapper {
    @Mapping(source = ORIGINAL_MEMBER_UUID, target = ACTIVE_MEMBER, qualifiedByName = "toActiveMember")
    @Mapping(source = ORIGINAL_MEMBER_UUID , target = ORIGINAL_MEMBER, qualifiedByName = "toOriginalMember")
    @Mapping(target = "memberAuthEntity", ignore = true)
    @Mapping(target = LOCKOUT_UNTIL, ignore = true)
    SiteMemberAuthEntity toMemberAuthEntity(SiteMemberAuthInsertRequest memberAuthInsertRequest, @Context SiteMemberRepository memberRepository);

    @Mapping(source = ACTIVE_MEMBER, target = ACTIVE_MEMBER_UUID, qualifiedByName = "toActiveMemberUuid")
    @Mapping(source = ORIGINAL_MEMBER, target = ORIGINAL_MEMBER_UUID, qualifiedByName = "toOriginalMemberUuid")
    SiteMemberAuthResponse toMemberAuthResponse(SiteMemberAuthEntity memberAuthEntity);

    @Named("toActiveMember")
    default SiteMemberEntity toActiveMember(UUID originalMemberUuid, @Context SiteMemberRepository memberRepository) {
        return memberRepository.findByUuid(originalMemberUuid).orElseThrow();
    }

    @Named("toOriginalMember")
    default SiteMemberEntity toOriginalMember(UUID originalMemberUuid, @Context SiteMemberRepository memberRepository) {
        return memberRepository.findByUuid(originalMemberUuid).orElseThrow();
    }

    @Named("toActiveMemberUuid")
    default UUID toActiveMemberUuid(SiteMemberEntity activeMemberEntity) {
        return activeMemberEntity.getUuid();
    }

    @Named("toOriginalMemberUuid")
    default UUID toOriginalMemberUuid(SiteMemberEntity originalMemberEntity) {
        return originalMemberEntity.getUuid();
    }
}
