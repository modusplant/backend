package kr.modusplant.modules.auth.social.app.service;

import kr.modusplant.modules.auth.social.app.dto.KakaoUserInfo;
import kr.modusplant.modules.auth.social.app.service.supers.SocialAuthClient;
import kr.modusplant.modules.auth.social.error.OAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthClient implements SocialAuthClient {
    private final RestClient.Builder restClientBuilder;

    @Value("${kakao.api-key}")
    private String KAKAO_API_KEY;
    @Value("${kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    public String getAccessToken(String code) {
        RestClient restClient = restClientBuilder
                .baseUrl("https://kauth.kakao.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        Map.of(
                "code",code,
                "client_id",KAKAO_API_KEY,
                "redirect_uri", KAKAO_REDIRECT_URI,
                "grant_type", "authorization_code"
        ).forEach(formData::add);

        return restClient.post()
                .uri("/oauth/token")
                .body(formData)
                .retrieve()
                .onStatus(this::isErrorStatus, (request, response) -> {
                    throw new OAuthException((HttpStatus) response.getStatusCode());
                })
                .body(Map.class)
                .get("access_token").toString();
    }

    public KakaoUserInfo getUserInfo(String accessToken) {
        RestClient restClient = restClientBuilder
                .baseUrl("https://kapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .build();

        return restClient.get()
                .uri("/v2/user/me")
                .retrieve()
                .onStatus(this::isErrorStatus, (request, response) -> {
                    throw new OAuthException((HttpStatus) response.getStatusCode());
                })
                .body(KakaoUserInfo.class);
    }

    private boolean isErrorStatus(HttpStatusCode status) {
        return status.equals(HttpStatus.BAD_REQUEST) ||
                status.equals(HttpStatus.UNAUTHORIZED) ||
                status.equals(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
