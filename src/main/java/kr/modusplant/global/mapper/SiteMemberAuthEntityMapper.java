package kr.modusplant.global.mapper;

import kr.modusplant.global.domain.model.SiteMemberAuth;
import kr.modusplant.global.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import org.mapstruct.*;

import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.*;

@Mapper
public interface SiteMemberAuthEntityMapper {
    @BeanMapping(ignoreByDefault = true)
    default SiteMemberAuthEntity createSiteMemberAuthEntity(SiteMemberAuth memberAuth,
                                                            @Context SiteMemberJpaRepository memberRepository) {
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
                                                            @Context SiteMemberJpaRepository memberRepository) {
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
