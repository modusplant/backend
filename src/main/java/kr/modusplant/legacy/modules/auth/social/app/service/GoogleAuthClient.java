package kr.modusplant.legacy.modules.auth.social.app.service;

import kr.modusplant.infrastructure.exception.enums.ErrorCode;
import kr.modusplant.legacy.modules.auth.social.app.dto.GoogleUserInfo;
import kr.modusplant.legacy.modules.auth.social.app.service.supers.SocialAuthClient;
import kr.modusplant.legacy.modules.auth.social.error.OAuthRequestFailException;
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
public class GoogleAuthClient implements SocialAuthClient {
    private final RestClient.Builder restClientBuilder;

    @Value("${google.api-key}")
    private String GOOGLE_API_KEY;
    @Value("${google.secret}")
    private String GOOGLE_SECRET;
    @Value("${google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;

    public String getAccessToken(String code) {
        RestClient restClient = restClientBuilder
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
                    throw new OAuthRequestFailException(ErrorCode.GOOGLE_LOGIN_FAIL, "google");
                })
                .body(Map.class)
                .get("access_token").toString();
    }

    public GoogleUserInfo getUserInfo(String accessToken) {
        RestClient restClient = restClientBuilder
                .baseUrl("https://www.googleapis.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .build();

        return restClient.get()
                .uri("/userinfo/v2/me")
                .retrieve()
                .onStatus(this::isErrorStatus, (request, response) -> {
                    throw new OAuthRequestFailException(ErrorCode.GOOGLE_LOGIN_FAIL, "google");
                })
                .body(GoogleUserInfo.class);
    }

    private boolean isErrorStatus(HttpStatusCode status) {
        return status.equals(HttpStatus.BAD_REQUEST) ||
                status.equals(HttpStatus.UNAUTHORIZED) ||
                status.equals(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
