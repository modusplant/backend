package kr.modusplant.modules.jwt.domain.service;

import kr.modusplant.modules.jwt.error.TokenNotFoundException;
import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenValidationService {
    private final RefreshTokenRepository tokenRepository;

    public void validateNotFoundTokenUuid(UUID uuid) {
        if (uuid == null || !tokenRepository.existsByUuid(uuid)) {
            throw new TokenNotFoundException();
        }
    }

    public void validateNotFoundRefreshToken(String refreshToken) {
        if (refreshToken == null || !tokenRepository.existsByRefreshToken(refreshToken)) {
            throw new TokenNotFoundException();
        }
    }
}
