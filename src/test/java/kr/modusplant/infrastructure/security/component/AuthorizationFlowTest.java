package kr.modusplant.infrastructure.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kr.modusplant.domains.comment.adapter.controller.CommentController;
import kr.modusplant.domains.comment.framework.in.web.rest.CommentRestController;
import kr.modusplant.domains.comment.support.utils.adapter.CommentRegisterRequestTestUtils;
import kr.modusplant.domains.comment.support.utils.adapter.CommentResponseTestUtils;
import kr.modusplant.domains.identity.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.framework.out.jpa.entity.constant.SiteMemberEntityConstant;
import kr.modusplant.framework.out.jpa.entity.constant.SiteMemberRoleEntityConstant;
import kr.modusplant.infrastructure.security.enums.SecurityErrorCode;
import kr.modusplant.legacy.domains.communication.common.util.domain.CommPostTestUtils;
import kr.modusplant.legacy.modules.jwt.app.service.TokenProvider;
import kr.modusplant.legacy.modules.jwt.persistence.repository.TokenRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationFlowTest implements
        SiteMemberEntityConstant, SiteMemberRoleEntityConstant,
        CommentRegisterRequestTestUtils, CommentResponseTestUtils, CommPostTestUtils {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private CommentRestController commentRestController;

    @MockitoBean
    private CommentController commentController;

    @MockitoBean
    private NormalIdentityMapper mapper;

    @MockitoBean
    private TokenRedisRepository tokenRedisRepository;

    private String rawAccessToken;
    private Claims accessTokenClaims;

    @BeforeEach
    void setUp() {
        rawAccessToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        accessTokenClaims = Jwts.claims()
                .subject(MEMBER_BASIC_USER_UUID.toString())
                .add("nickname", MEMBER_BASIC_USER_NICKNAME)
                .add("roles", MEMBER_ROLE_USER_ROLE)
                .build();
    }

    @Test
    public void testCommentApiWithRole_givenMatchingRole_willReturnSuccessResponse() throws Exception {
        // given
        given(tokenRedisRepository.isBlacklisted(rawAccessToken.substring(7))).willReturn(false);
        given(tokenProvider.validateToken(rawAccessToken.substring(7))).willReturn(true);
        given(tokenProvider.getClaimsFromToken(rawAccessToken.substring(7))).willReturn(accessTokenClaims);
        doNothing().when(commentController).register(testCommentRegisterRequest);

        // when
        mockMvc.perform(post("/api/v1/communication/comments")
                        .header("Authorization", rawAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCommentRegisterRequest)).characterEncoding("UTF-8"))

                // then
                .andExpect(status().isOk());
    }

    @Test
    public void testMonitorApiWithRole_givenMismatchingRole_willReturnErrorResponse() throws Exception {
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
