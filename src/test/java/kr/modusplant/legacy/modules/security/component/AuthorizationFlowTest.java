package kr.modusplant.legacy.modules.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kr.modusplant.legacy.domains.communication.app.http.request.CommCommentInsertRequest;
import kr.modusplant.legacy.domains.communication.app.http.response.CommCommentResponse;
import kr.modusplant.legacy.domains.communication.app.service.CommCommentApplicationService;
import kr.modusplant.legacy.domains.communication.common.util.app.http.request.CommCommentInsertRequestTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.app.http.response.CommCommentResponseTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.domain.CommPostTestUtils;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberRoleTestUtils;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.legacy.modules.jwt.app.service.TokenProvider;
import kr.modusplant.legacy.modules.jwt.persistence.repository.TokenRedisRepository;
import kr.modusplant.legacy.modules.security.enums.SecurityErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationFlowTest implements
        SiteMemberTestUtils, SiteMemberRoleTestUtils,
        CommCommentInsertRequestTestUtils, CommPostTestUtils, CommCommentResponseTestUtils {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private CommCommentApplicationService commCommentApplicationService;

    @MockitoBean
    private TokenRedisRepository tokenRedisRepository;

    private String rawAccessToken;
    private Claims accessTokenClaims;

    @BeforeEach
    void setUp() {
        rawAccessToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        accessTokenClaims = Jwts.claims()
                .subject(memberBasicUserWithUuid.getUuid().toString())
                .add("nickname", memberBasicUserWithUuid.getNickname())
                .add("roles", memberRoleUser.getRole())
                .build();
    }

    @Test
    public void givenMatchingRole_willReturnSuccessResponse() throws Exception {
        // given
        CommCommentInsertRequest commentInsertRequest =
                createCommCommentInsertRequest(TEST_COMM_POST_WITH_ULID.getUlid());
        CommCommentResponse commentResponse = createCommCommentResponse(
                TEST_COMM_POST_WITH_ULID.getUlid(), memberBasicUserWithUuid.getUuid(), memberBasicUserWithUuid.getNickname()
        );

        given(tokenRedisRepository.isBlacklisted(rawAccessToken.substring(7))).willReturn(false);
        given(tokenProvider.validateToken(rawAccessToken.substring(7))).willReturn(true);
        given(tokenProvider.getClaimsFromToken(rawAccessToken.substring(7))).willReturn(accessTokenClaims);
        given(commCommentApplicationService.insert(commentInsertRequest, memberBasicUserWithUuid.getUuid())).willReturn(commentResponse);

        // when
        mockMvc.perform(post("/api/v1/communication/comments")
                        .header("Authorization", rawAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentInsertRequest)).characterEncoding("UTF-8"))

                // then
                .andExpect(status().isOk());
    }

    @Test
    public void givenMismatchingRole_willReturnErrorResponse() throws Exception {
        // given
        given(tokenRedisRepository.isBlacklisted(rawAccessToken.substring(7))).willReturn(false);
        given(tokenProvider.validateToken(rawAccessToken.substring(7))).willReturn(true);
        given(tokenProvider.getClaimsFromToken(rawAccessToken.substring(7))).willReturn(accessTokenClaims);

        // when
        mockMvc.perform(get("/api/monitor/monitor-success")
                        .header("Authorization", rawAccessToken))

                // then
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(SecurityErrorCode.ACCESS_DENIED.getHttpStatus().getValue()))
                .andExpect(jsonPath("$.code").value(SecurityErrorCode.ACCESS_DENIED.getCode()))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
