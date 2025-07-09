package kr.modusplant.modules.jwt.app.service;

import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.EntityNotFoundException;
import kr.modusplant.global.vo.EntityName;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.modules.jwt.mapper.RefreshTokenAppInfraMapper;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenRepository;
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
    private final SiteMemberRepository memberRepository;
    private final RefreshTokenAppInfraMapper refreshTokenAppInfraMapper;

    public Optional<RefreshToken> getByUuid(UUID uuid) {
        Optional<RefreshTokenEntity> tokenOrEmpty = tokenRepository.findByUuid(uuid);
        return tokenOrEmpty.isEmpty() ? Optional.empty() : Optional.of(refreshTokenAppInfraMapper.toRefreshToken(tokenOrEmpty.orElseThrow()));
    }

    public Optional<RefreshToken> getByMemberUuidAndRefreshToken(UUID uuid, String refreshToken) {
        Optional<RefreshTokenEntity> tokenOrEmpty = tokenRepository.findByMemberAndRefreshToken(
                memberRepository.findByUuid(uuid).orElseThrow(() -> new EntityNotFoundException(ErrorCode.SITEMEMBER_NOT_FOUND, EntityName.SITE_MEMBER)), refreshToken);
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
