package kr.modusplant.domains.account.social.framework.outbound.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.framework.outbound.client.dto.IdTokenInfo;
import kr.modusplant.domains.account.social.framework.outbound.client.dto.OAuthErrorResponse;
import kr.modusplant.domains.account.social.framework.outbound.client.dto.SocialToken;
import kr.modusplant.domains.account.social.framework.outbound.exception.OAuthRequestFailException;
import kr.modusplant.domains.account.social.usecase.port.client.SocialAuthClient;
import kr.modusplant.domains.account.social.usecase.record.SocialUserInfo;
import kr.modusplant.shared.exception.model.DynamicErrorCode;
import kr.modusplant.shared.exception.supers.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthClient implements SocialAuthClient {
    private final SocialIdTokenParser idTokenParser;
    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${kakao.api-key}")
    private String KAKAO_API_KEY;
    @Value("${kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;
    @Value("${kakao.local-redirect-uri:#{null}}")
    private String KAKAO_LOCAL_REDIRECT_URI;


    @Override
    public SocialUserInfo getToken(String code, boolean isLocal) {
        RestClient restClient = restClientBuilder
                .baseUrl("https://kauth.kakao.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        String redirectUri = (isLocal && KAKAO_LOCAL_REDIRECT_URI != null)
                ? KAKAO_LOCAL_REDIRECT_URI
                : KAKAO_REDIRECT_URI;

        MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        Map.of(
                "code",code,
                "client_id",KAKAO_API_KEY,
                "redirect_uri", redirectUri,
                "grant_type", "authorization_code"
        ).forEach(formData::add);

        SocialToken socialToken = restClient.post()
                .uri("/oauth/token")
                .body(formData)
                .retrieve()
                .onStatus(this::isErrorStatus, (request, response)
                        -> handleKakaoError(SocialIdentityErrorCode.KAKAO_LOGIN_FAIL, response))
                .body(SocialToken.class);

        IdTokenInfo idTokenInfo = idTokenParser.parse(socialToken.idToken(), SocialProvider.KAKAO);

        return new SocialUserInfo(socialToken.accessToken(), idTokenInfo.id(), idTokenInfo.email(), idTokenInfo.nickname());
    }

    @Override
    public void revokeAccess(String accessToken) {
        RestClient restClient = restClientBuilder
                .baseUrl("https://kapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        restClient.post()
                .uri("/v1/user/unlink")
                .retrieve()
                .onStatus(this::isErrorStatus,(request,response)
                        -> handleKakaoError(SocialIdentityErrorCode.KAKAO_REVOKE_FAIL, response))
                .toBodilessEntity();
    }

    private boolean isErrorStatus(HttpStatusCode status) {
        return status.equals(HttpStatus.BAD_REQUEST) ||
                status.equals(HttpStatus.UNAUTHORIZED) ||
                status.equals(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void handleKakaoError(ErrorCode errorCode, ClientHttpResponse response) throws IOException {
        try (InputStream body = response.getBody()) {
            OAuthErrorResponse errorResponse = objectMapper.readValue(body, OAuthErrorResponse.class);
            log.error("[{}] OAuth error - {}: {}", "kakao", errorResponse.error(), errorResponse.errorDescription());
            DynamicErrorCode dynamicErrorCode = DynamicErrorCode.create(
                    errorCode,
                    String.format("%s - [%s]", errorCode.getMessage(), errorResponse.error())
            );
            throw new OAuthRequestFailException(dynamicErrorCode, "kakao");
        }
    }
}
