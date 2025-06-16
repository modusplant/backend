package kr.modusplant.modules.jwt.domain.service;

import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static kr.modusplant.domains.member.vo.MemberUuid.MEMBER_UUID;
import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenValidationService {
    private final RefreshTokenRepository tokenRepository;
    private final SiteMemberRepository memberRepository;

    public void validateNotFoundMemberUuid(UUID memberUuid) {
        if (memberUuid == null || !memberRepository.existsByUuid(memberUuid)) {
            throw new EntityNotFoundException(getFormattedExceptionMessage(NOT_FOUND_ENTITY, MEMBER_UUID, memberUuid, SiteMemberEntity.class));
        }
    }

    public void validateNotFoundTokenUuid(UUID uuid) {
        if (uuid == null || !tokenRepository.existsByUuid(uuid)) {
            throw new EntityNotFoundWithUuidException(uuid, RefreshTokenEntity.class);
        }
    }

    public void validateNotFoundRefreshToken(String refreshToken) {
        if (refreshToken == null || !tokenRepository.existsByRefreshToken(refreshToken)) {
            throw new EntityNotFoundException(getFormattedExceptionMessage(NOT_FOUND_ENTITY, "refreshToken", refreshToken, RefreshTokenEntity.class));
        }
    }
}
