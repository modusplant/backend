package kr.modusplant.api.crud.service;

import kr.modusplant.api.crud.model.external.GoogleUserInfo;
import kr.modusplant.api.crud.model.external.KakaoUserInfo;
import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.model.SiteMemberAuth;
import kr.modusplant.global.domain.model.SiteMemberRole;
import kr.modusplant.global.domain.service.crud.SiteMemberAuthService;
import kr.modusplant.global.domain.service.crud.SiteMemberRoleService;
import kr.modusplant.global.domain.service.crud.SiteMemberService;
import kr.modusplant.global.enums.AuthProvider;
import kr.modusplant.global.enums.Role;
import kr.modusplant.global.error.OAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialAuthService {
    private final SiteMemberService siteMemberService;
    private final SiteMemberAuthService siteMemberAuthService;
    private final SiteMemberRoleService siteMemberRoleService;
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

    @Transactional
    public SiteMember findOrCreateMember(AuthProvider provider,String id, String email, String nickname) {
        // provider와 provider_id로 site_member_auth 사용자 조회
        Optional<SiteMemberAuth> existedMemberAuth = siteMemberAuthService.getByProviderAndProviderId(provider,id)
                .stream()
                .findFirst();

        // 신규 멤버 저장 및 멤버 반환
        return existedMemberAuth.map(siteMemberAuth -> {
            return siteMemberService.getByUuid(siteMemberAuth.getActiveMemberUuid()).get();
        }).orElseGet(() -> {
            SiteMember savedMember = createSiteMember(nickname);
            createSiteMemberAuth(savedMember.getUuid(),provider,id,email);
            createSiteMemberRole(savedMember.getUuid());
            return savedMember;
        });
    }

    private SiteMember createSiteMember(String nickname) {
        SiteMember siteMember = SiteMember.builder()
                .uuid(UUID.randomUUID())
                .nickname(nickname)
                .loggedInAt(LocalDateTime.now())
                .build();
        return siteMemberService.insert(siteMember);
    }

    private SiteMemberAuth createSiteMemberAuth(UUID memberUuid, AuthProvider provider, String id, String email) {
        SiteMemberAuth siteMemberAuth = SiteMemberAuth.builder()
                .uuid(UUID.randomUUID())
                .activeMemberUuid(memberUuid)
                .originalMemberUuid(memberUuid)
                .email(email)
                .provider(provider)
                .providerId(id)
                .build();
        return siteMemberAuthService.insert(siteMemberAuth);
    }

    private SiteMemberRole createSiteMemberRole(UUID memberUuid) {
        SiteMemberRole siteMemberRole = SiteMemberRole.builder()
                .uuid(memberUuid)
                .role(Role.ROLE_USER)
                .build();
        return siteMemberRoleService.insert(siteMemberRole);
    }

    private boolean isErrorStatus(HttpStatusCode status) {
        return status.equals(HttpStatus.BAD_REQUEST) ||
                status.equals(HttpStatus.UNAUTHORIZED) ||
                status.equals(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
