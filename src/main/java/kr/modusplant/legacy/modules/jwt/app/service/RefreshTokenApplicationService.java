package kr.modusplant.legacy.modules.jwt.app.service;

import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.legacy.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.legacy.modules.jwt.mapper.RefreshTokenAppInfraMapper;
import kr.modusplant.legacy.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.legacy.modules.jwt.persistence.repository.RefreshTokenRepository;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class RefreshTokenApplicationService {

    private final RefreshTokenRepository tokenRepository;
    private final SiteMemberJpaRepository memberRepository;
    private final RefreshTokenAppInfraMapper refreshTokenAppInfraMapper;

    public Optional<RefreshToken> getByUuid(UUID uuid) {
        Optional<RefreshTokenEntity> tokenOrEmpty = tokenRepository.findByUuid(uuid);
        return tokenOrEmpty.isEmpty() ? Optional.empty() : Optional.of(refreshTokenAppInfraMapper.toRefreshToken(tokenOrEmpty.orElseThrow()));
    }

    public Optional<RefreshToken> getByMemberUuidAndRefreshToken(UUID uuid, String refreshToken) {
        Optional<RefreshTokenEntity> tokenOrEmpty = tokenRepository.findByMemberAndRefreshToken(
                memberRepository.findByUuid(uuid).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, EntityName.SITE_MEMBER)), refreshToken);
        return tokenOrEmpty.isEmpty() ? Optional.empty() : Optional.of(refreshTokenAppInfraMapper.toRefreshToken(tokenOrEmpty.orElseThrow()));
    }

    public Optional<RefreshToken> getByRefreshToken(String refreshToken) {
        Optional<RefreshTokenEntity> tokenOrEmpty = tokenRepository.findByRefreshToken(refreshToken);
        return tokenOrEmpty.isEmpty() ? Optional.empty() : Optional.of(refreshTokenAppInfraMapper.toRefreshToken(tokenOrEmpty.orElseThrow()));
    }

    @Transactional
    public RefreshToken insert(RefreshToken refreshToken) {
        return refreshTokenAppInfraMapper.toRefreshToken(tokenRepository.save(refreshTokenAppInfraMapper.toRefreshTokenEntity(refreshToken,memberRepository)));
    }

    @Transactional
    public void removeByUuid(UUID uuid) {
        tokenRepository.deleteByUuid(uuid);
    }
}
