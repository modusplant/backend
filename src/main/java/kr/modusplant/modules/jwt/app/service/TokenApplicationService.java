package kr.modusplant.modules.jwt.app.service;

import kr.modusplant.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberRoleApplicationService;
import kr.modusplant.global.enums.Role;
import kr.modusplant.modules.jwt.app.dto.TokenPair;
import kr.modusplant.modules.jwt.domain.model.RefreshToken;
import kr.modusplant.modules.jwt.domain.service.TokenValidationService;
import kr.modusplant.modules.jwt.error.InvalidTokenException;
import kr.modusplant.modules.jwt.error.TokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static kr.modusplant.global.vo.CamelCaseWord.MEMBER_UUID;

@Service
@RequiredArgsConstructor
public class TokenApplicationService {
    private final TokenProvider tokenProvider;
    private final SiteMemberApplicationService memberApplicationService;
    private final SiteMemberRoleApplicationService memberRoleApplicationService;
    private final RefreshTokenApplicationService refreshTokenApplicationService;
    private final TokenValidationService tokenValidationService;

    // 토큰 생성
    public TokenPair issueToken(UUID memberUuid, String nickname, Role role) {
        // memberUuid 검증
        tokenValidationService.validateNotFoundMemberUuid(MEMBER_UUID, memberUuid);

        // accessToken , refresh token 생성
        Map<String,String> claims = new HashMap<>();
        claims.put("nickname",nickname);
        claims.put("role",role.getValue());

        String accessToken = tokenProvider.generateAccessToken(memberUuid, claims);
        String refreshToken = tokenProvider.generateRefreshToken(memberUuid);

        // refresh token DB에 저장
        refreshTokenApplicationService.insert(
                RefreshToken.builder()
                        .memberUuid(memberUuid)
                        .refreshToken(refreshToken)
                        .issuedAt(tokenProvider.getIssuedAtFromToken(refreshToken))
                        .expiredAt(tokenProvider.getExpirationFromToken(refreshToken))
                        .build());


        return new TokenPair(accessToken,refreshToken);
    }

    // 토큰 갱신
    public TokenPair reissueToken(String refreshToken) {
        // refresh token 검증
        if(!tokenProvider.validateToken(refreshToken))
            throw new InvalidTokenException("The Refresh Token has expired. Please log in again.");
        tokenValidationService.validateNotFoundRefreshToken(refreshToken);

        // token에서 사용자 정보 가져오기
        UUID memberUuid = tokenProvider.getMemberUuidFromToken(refreshToken);
        SiteMemberResponse siteMember = memberApplicationService.getByUuid(memberUuid)
                .orElseThrow(() -> new TokenNotFoundException("Failed to find Site member"));
        SiteMemberRoleResponse siteMemberRole = memberRoleApplicationService.getByUuid(memberUuid)
                .orElseThrow(() -> new TokenNotFoundException("Failed to find Site member role"));

        // refresh token 재발급 (RTR기법)
        String reissuedRefreshToken = tokenProvider.generateRefreshToken(memberUuid);
        refreshTokenApplicationService.insert(
                RefreshToken.builder()
                        .uuid(refreshTokenApplicationService.getByRefreshToken(refreshToken).orElseThrow().getUuid())
                        .memberUuid(memberUuid)
                        .refreshToken(reissuedRefreshToken)
                        .issuedAt(tokenProvider.getIssuedAtFromToken(reissuedRefreshToken))
                        .expiredAt(tokenProvider.getExpirationFromToken(reissuedRefreshToken))
                        .build());

        // access token 재발급
        String accessToken = tokenProvider.generateAccessToken(memberUuid,claims);

        return TokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 토큰 삭제
    public void removeToken(String refreshToken) {
        // 토큰 검증
        tokenProvider.validateToken(refreshToken);
        tokenValidationService.validateNotFoundRefreshToken(refreshToken);
        UUID memberUuid = tokenProvider.getMemberUuidFromToken(refreshToken);
        RefreshToken token = refreshTokenApplicationService.getByMemberUuidAndRefreshToken(memberUuid,refreshToken)
                .orElseThrow(() -> new TokenNotFoundException("Failed to find Refresh Token"));
        // 토큰 삭제
        refreshTokenApplicationService.removeByUuid(token.getUuid());
    }

    // 토큰 검증
    public TokenPair verifyAndReissueToken(String accessToken, String refreshToken) {
        if (tokenProvider.validateToken(accessToken)) {
            return TokenPair.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        }
        tokenProvider.validateToken(refreshToken);

        return reissueToken(refreshToken);
    }
}
