package kr.modusplant.global.mapper;

import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.global.domain.model.SiteMemberAuth;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.global.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import org.mapstruct.*;

import java.util.UUID;

import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.util.MapperUtils.map;
import static kr.modusplant.global.vo.CamelCaseWord.*;
import static kr.modusplant.global.vo.ExceptionMessage.NOT_FOUND_ENTITY;
import static kr.modusplant.global.vo.SnakeCaseWord.SNAKE_ORIGINAL_MEMBER_UUID;

@Mapper
public interface SiteMemberAuthEntityMapper {
    @BeanMapping(ignoreByDefault = true)
    default SiteMemberAuthEntity createSiteMemberAuthEntity(SiteMemberAuth memberAuth,
                                                            @Context SiteMemberJpaRepository memberRepository) {
        return SiteMemberAuthEntity.builder()
                .activeMember(memberRepository.findByUuid(memberAuth.getActiveMemberUuid())
                        .orElseThrow(() -> new EntityNotFoundWithUuidException(memberAuth.getUuid(), SiteMemberEntity.class)))
                .originalMember(memberRepository.findByUuid(memberAuth.getOriginalMemberUuid())
                        .orElseThrow(() -> new EntityNotFoundWithUuidException(memberAuth.getUuid(), SiteMemberEntity.class)))
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
                                                            @Context SiteMemberAuthJpaRepository memberAuthRepository,
                                                            @Context SiteMemberJpaRepository memberRepository) {
        return map(memberAuth, memberAuthRepository.findByOriginalMember(
                        memberRepository.findByUuid(memberAuth.getOriginalMemberUuid())
                                .orElseThrow(() -> new EntityNotFoundWithUuidException(
                                        memberAuth.getOriginalMemberUuid(), SiteMemberEntity.class)))
                .orElseThrow(() -> new EntityNotFoundException(getFormattedExceptionMessage(
                        NOT_FOUND_ENTITY, SNAKE_ORIGINAL_MEMBER_UUID, memberAuth.getOriginalMemberUuid(), SiteMemberAuthEntity.class))), memberRepository);
    }

    @Mapping(source = ACTIVE_MEMBER, target = ACTIVE_MEMBER_UUID, qualifiedByName = "toActiveMemberUuid")
    @Mapping(source = ORIGINAL_MEMBER, target = ORIGINAL_MEMBER_UUID, qualifiedByName = "toOriginalMemberUuid")
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
