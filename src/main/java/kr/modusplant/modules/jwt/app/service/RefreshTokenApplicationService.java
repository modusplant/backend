package kr.modusplant.modules.jwt.app.service;

import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.modules.jwt.mapper.entity.RefreshTokenEntityMapper;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenJpaRepository;
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

    private final RefreshTokenJpaRepository tokenRepository;
    private final SiteMemberRepository memberRepository;
    private final RefreshTokenEntityMapper refreshTokenEntityMapper;

    public Optional<RefreshToken> getByUuid(UUID uuid) {
        Optional<RefreshTokenEntity> tokenOrEmpty = tokenRepository.findByUuid(uuid);
        return tokenOrEmpty.isEmpty() ? Optional.empty() : Optional.of(refreshTokenEntityMapper.toRefreshToken(tokenOrEmpty.orElseThrow()));
    }

    public Optional<RefreshToken> getByMemberUuidAndDeviceId(UUID uuid, UUID deviceId) {
        Optional<RefreshTokenEntity> tokenOrEmpty = tokenRepository.findByMemberAndDeviceId(
                memberRepository.findByUuid(uuid)
                        .orElseThrow(() -> new EntityNotFoundWithUuidException(uuid, RefreshTokenEntity.class)),
                deviceId
        );
        return tokenOrEmpty.isEmpty() ? Optional.empty() : Optional.of(refreshTokenEntityMapper.toRefreshToken(tokenOrEmpty.orElseThrow()));
    }

    public Optional<RefreshToken> getByRefreshToken(String refreshToken) {
        Optional<RefreshTokenEntity> tokenOrEmpty = tokenRepository.findByRefreshToken(refreshToken);
        return tokenOrEmpty.isEmpty() ? Optional.empty() : Optional.of(refreshTokenEntityMapper.toRefreshToken(tokenOrEmpty.orElseThrow()));
    }

    public Optional<RefreshToken> getByDeviceId(UUID deviceId) {
        Optional<RefreshTokenEntity> tokenOrEmpty = tokenRepository.findByDeviceId(deviceId);
        return tokenOrEmpty.isEmpty() ? Optional.empty() : Optional.of(refreshTokenEntityMapper.toRefreshToken(tokenOrEmpty.orElseThrow()));
    }

    @Transactional
    public RefreshToken insert(RefreshToken refreshToken) {
        return refreshTokenEntityMapper.toRefreshToken(tokenRepository.save(refreshTokenEntityMapper.createRefreshTokenEntity(refreshToken,memberRepository)));
    }

    @Transactional
    public RefreshToken update(RefreshToken refreshToken) {
        return refreshTokenEntityMapper.toRefreshToken(tokenRepository.save(refreshTokenEntityMapper.updateRefreshTokenEntity(refreshToken,memberRepository)));
    }

    @Transactional
    public void removeByUuid(UUID uuid) {
        tokenRepository.deleteByUuid(uuid);
    }
}
