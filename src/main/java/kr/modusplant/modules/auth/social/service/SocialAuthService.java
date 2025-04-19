package kr.modusplant.modules.auth.social.service;

import kr.modusplant.modules.auth.social.error.OAuthException;
import kr.modusplant.modules.auth.social.dto.KakaoUserInfo;
import kr.modusplant.modules.auth.social.dto.GoogleUserInfo;
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
public class SocialAuthService {
    private RestClient restClient;

    @Value("${kakao.api-key}")
    private String KAKAO_API_KEY;
    @Value("${kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;
    @Value("${google.api-key}")
    private String GOOGLE_API_KEY;
    @Value("${google.secret}")
    private String GOOGLE_SECRET;
    @Value("${google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;


    public String getKakaoAccessToken(String code) {
        restClient = RestClient.builder()
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

    public String getGoogleAccessToken(String code) {
        restClient = RestClient.builder()
                .baseUrl("https://oauth2.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        Map.of(
                "code", code,
                "client_id", GOOGLE_API_KEY,
                "client_secret", GOOGLE_SECRET,
                "redirect_uri", GOOGLE_REDIRECT_URI,
                "grant_type","authorization_code"
        ).forEach(formData::add);

        return restClient.post()
                .uri("/token")
                .body(formData)
                .retrieve()
                .onStatus(this::isErrorStatus, (request, response) -> {
                    throw new OAuthException((HttpStatus) response.getStatusCode());
                })
                .body(Map.class)
                .get("access_token").toString();
    }

    public KakaoUserInfo getKakaoUserInfo(String accessToken) {
        restClient = RestClient.builder()
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

    public GoogleUserInfo getGoogleUserInfo(String accessToken) {
        restClient = RestClient.builder()
                .baseUrl("https://www.googleapis.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .build();

        return restClient.get()
                .uri("/userinfo/v2/me")
                .retrieve()
                .onStatus(this::isErrorStatus, (request, response) -> {
                    throw new OAuthException((HttpStatus) response.getStatusCode());
                })
                .body(GoogleUserInfo.class);
    }

    private boolean isErrorStatus(HttpStatusCode status) {
        return status.equals(HttpStatus.BAD_REQUEST) ||
                status.equals(HttpStatus.UNAUTHORIZED) ||
                status.equals(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
