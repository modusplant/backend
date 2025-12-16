package kr.modusplant.infrastructure.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import kr.modusplant.domains.account.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.comment.adapter.controller.CommentController;
import kr.modusplant.domains.comment.common.util.adapter.CommentRegisterRequestTestUtils;
import kr.modusplant.domains.comment.common.util.adapter.CommentResponseTestUtils;
import kr.modusplant.domains.comment.framework.in.web.rest.CommentRestController;
import kr.modusplant.infrastructure.jwt.framework.out.redis.AccessTokenRedisRepository;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationFlowTest implements CommentRegisterRequestTestUtils, CommentResponseTestUtils {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtTokenProvider tokenProvider;

    @MockitoBean
    private CommentRestController commentRestController;

    @MockitoBean
    private CommentController commentController;

    @MockitoBean
    private NormalIdentityMapper mapper;

    @MockitoBean
    private AccessTokenRedisRepository tokenRedisRepository;

    private String rawAccessToken;
    private Claims accessTokenClaims;

//    @BeforeEach
//    void setUp() {
//        rawAccessToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
//        accessTokenClaims = Jwts.claims()
//                .subject(MEMBER_BASIC_USER_UUID.toString())
//                .add("nickname", MEMBER_BASIC_USER_NICKNAME)
//                .add("roles", MEMBER_ROLE_USER_ROLE)
//                .build();
//    }
//
//    @Test
//    public void testCommentApiWithRole_givenMatchingRole_willReturnSuccessResponse() throws Exception {
//        // given
//        given(tokenRedisRepository.isBlacklisted(rawAccessToken.substring(7))).willReturn(false);
//        given(tokenProvider.validateToken(rawAccessToken.substring(7))).willReturn(true);
//        given(tokenProvider.getClaimsFromToken(rawAccessToken.substring(7))).willReturn(accessTokenClaims);
//        doNothing().when(commentController).register(testCommentRegisterRequest);
//
//        // when
//        mockMvc.perform(post("/api/v1/communication/comments")
//                        .header("Authorization", rawAccessToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(testCommentRegisterRequest)).characterEncoding("UTF-8"))
//
//                // then
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testMonitorApiWithRole_givenMismatchingRole_willReturnErrorResponse() throws Exception {
//        // given
//        given(tokenRedisRepository.isBlacklisted(rawAccessToken.substring(7))).willReturn(false);
//        given(tokenProvider.validateToken(rawAccessToken.substring(7))).willReturn(true);
//        given(tokenProvider.getClaimsFromToken(rawAccessToken.substring(7))).willReturn(accessTokenClaims);
//
//        // when
//        mockMvc.perform(get("/api/monitor/monitor-success")
//                        .header("Authorization", rawAccessToken))
//
//                // then
//                .andExpect(status().isForbidden())
//                .andExpect(jsonPath("$.status").value(SecurityErrorCode.ACCESS_DENIED.getHttpStatus().getValue()))
//                .andExpect(jsonPath("$.code").value(SecurityErrorCode.ACCESS_DENIED.getCode()))
//                .andExpect(jsonPath("$.message").isNotEmpty());
//    }
}
