package kr.modusplant.api.crud.member.mapper;

import kr.modusplant.api.crud.member.domain.model.SiteMemberAuth;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberCrudJpaRepository;
import org.mapstruct.*;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.*;

@Mapper
public interface SiteMemberAuthEntityMapper {
    @BeanMapping(ignoreByDefault = true)
    default SiteMemberAuthEntity createSiteMemberAuthEntity(SiteMemberAuth memberAuth,
                                                            @Context SiteMemberCrudJpaRepository memberRepository) {
        return SiteMemberAuthEntity.builder()
                .activeMember(memberRepository.findByUuid(memberAuth.getActiveMemberUuid()).orElseThrow())
                .originalMember(memberRepository.findByUuid(memberAuth.getOriginalMemberUuid()).orElseThrow())
                .email(memberAuth.getEmail())
                .pw(memberAuth.getPw())
                .provider(memberAuth.getProvider())
                .providerId(memberAuth.getProviderId())
                .failedAttempt(memberAuth.getFailedAttempt())
                .lockoutRefreshAt(memberAuth.getLockoutRefreshAt())
                .lockoutUntil(memberAuth.getLockoutUntil()).build();
    }

    @BeanMapping(ignoreByDefault = true)
    default SiteMemberAuthEntity updateSiteMemberAuthEntity(SiteMemberAuth memberAuth,
                                                            @Context SiteMemberCrudJpaRepository memberRepository) {
        return SiteMemberAuthEntity.builder()
                .uuid(memberAuth.getUuid())
                .activeMember(memberRepository.findByUuid(memberAuth.getActiveMemberUuid()).orElseThrow())
                .originalMember(memberRepository.findByUuid(memberAuth.getOriginalMemberUuid()).orElseThrow())
                .email(memberAuth.getEmail())
                .pw(memberAuth.getPw())
                .provider(memberAuth.getProvider())
                .providerId(memberAuth.getProviderId())
                .failedAttempt(memberAuth.getFailedAttempt())
                .lockoutRefreshAt(memberAuth.getLockoutRefreshAt())
                .lockoutUntil(memberAuth.getLockoutUntil()).build();
    }

    @Mapping(source = ACTIVE_MEMBER, target = ACTIVE_MEMBER_UUID, qualifiedByName = "toActiveMemberUuid")
    @Mapping(source = ORIGINAL_MEMBER, target = ORIGINAL_MEMBER_UUID, qualifiedByName = "toOriginalMemberUuid")
    @Mapping(target = MEMBER_AUTH, ignore = true)
    SiteMemberAuth toSiteMemberAuth(SiteMemberAuthEntity memberAuthEntity);

    @Named("toActiveMemberUuid")
    default UUID toActiveMemberUuid(SiteMemberEntity activeMember) {
        return activeMember.getUuid();
    }

    @Named("toOriginalMemberUuid")
    default UUID toOriginalMemberUuid(SiteMemberEntity originalMember) {
        return originalMember.getUuid();
    }
}
