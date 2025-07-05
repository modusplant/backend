package kr.modusplant.global.middleware.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCommentResponse;
import kr.modusplant.domains.communication.conversation.app.service.ConvCommentApplicationService;
import kr.modusplant.domains.communication.conversation.common.util.app.http.request.ConvCommentInsertRequestTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.app.http.response.ConvCommentResponseTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.domain.ConvPostTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberRoleTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.global.middleware.security.config.SecurityConfig;
import kr.modusplant.modules.jwt.app.service.TokenProvider;
import kr.modusplant.modules.jwt.persistence.repository.TokenRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
@Import(SecurityConfig.class)
public class AuthorizationFlowTest implements
        SiteMemberTestUtils, SiteMemberRoleTestUtils,
        ConvCommentInsertRequestTestUtils, ConvPostTestUtils, ConvCommentResponseTestUtils {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private ConvCommentApplicationService convCommentApplicationService;

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
        ConvCommentInsertRequest commentInsertRequest =
                createConvCommentInsertRequest(testConvPostWithUlid.getUlid());
        ConvCommentResponse commentResponse = createConvCommentResponse(
                testConvPostWithUlid.getUlid(), memberBasicUserWithUuid.getUuid(), memberBasicUserWithUuid.getNickname()
        );

        given(tokenRedisRepository.isBlacklisted(rawAccessToken.substring(7))).willReturn(false);
        given(tokenProvider.validateToken(rawAccessToken.substring(7))).willReturn(true);
        given(tokenProvider.getClaimsFromToken(rawAccessToken.substring(7))).willReturn(accessTokenClaims);
        given(convCommentApplicationService.insert(commentInsertRequest, memberBasicUserWithUuid.getUuid())).willReturn(commentResponse);

        // when
        mockMvc.perform(post("/api/v1/conversation/comments")
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
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
