package kr.modusplant.global.middleware.security.integration;

import io.jsonwebtoken.Jwts;
import kr.modusplant.global.middleware.security.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.security.*;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityConfig.class})
public class NormalLogoutFlowTest {

    @Autowired
    private MockMvc mockMvc;

    private PublicKey publicKey;
    private String refreshToken;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        refreshToken = createTestRefreshToken();
    }

    @Test
    public void givenRefreshToken_willCallSuccessHandler() throws Exception {
        String rawRefreshToken = "Bearer " + refreshToken;

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", rawRefreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    private String createTestRefreshToken() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(256);
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        Date now = new Date();
        Date iat = new Date(now.getTime());
        Date exp = new Date(iat.getTime() + 30000000);

        return Jwts.builder()
                .issuer("https://test.issuer.com")
                .subject(String.valueOf(UUID.randomUUID()))
                .audience().add("https://test.audience.com").and()
                .issuedAt(iat)
                .expiration(exp)
                .signWith(privateKey)
                .compact();
    }
}
