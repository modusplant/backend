package kr.modusplant.modules.jwt.domain.service;

import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.modules.jwt.domain.service.supers.RefreshTokenApplicationService;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenValidationService {

    private final RefreshTokenApplicationService refreshTokenCrudService;
    private final RefreshTokenJpaRepository tokenRepository;
    private final SiteMemberRepository memberRepository;

    public boolean validateNotFoundRefreshToken(String refreshToken) {
        return refreshTokenCrudService.getByRefreshToken(refreshToken).isEmpty();
    }

    public boolean validateExistedDeviceId(UUID deviceId) {
        return refreshTokenCrudService.getByDeviceId(deviceId).isPresent();
    }

    public void validateNotFoundMemberUuid(String name, UUID memberUuid) {
        if (memberUuid == null || memberRepository.findByUuid(memberUuid).isEmpty()) {
            throw new EntityNotFoundException(getFormattedExceptionMessage(NOT_FOUND_ENTITY.getValue(), name, memberUuid, SiteMemberEntity.class));
        }
    }

    public void validateNotFoundTokenUuid(UUID uuid) {
        if (uuid == null || tokenRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, RefreshTokenEntity.class);
        }
    }
}
