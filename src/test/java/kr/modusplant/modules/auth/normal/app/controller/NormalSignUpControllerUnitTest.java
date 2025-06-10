package kr.modusplant.modules.auth.normal.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.member.common.util.app.http.response.SiteMemberAuthResponseTestUtils;
import kr.modusplant.domains.member.common.util.app.http.response.SiteMemberResponseTestUtils;
import kr.modusplant.domains.member.common.util.app.http.response.SiteMemberTermResponseTestUtils;
import kr.modusplant.modules.auth.normal.signup.app.http.request.NormalSignUpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
public class NormalSignUpControllerUnitTest implements SiteMemberResponseTestUtils, SiteMemberAuthResponseTestUtils, SiteMemberTermResponseTestUtils {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void saveMember_givenValidInput_thenReturn200() throws Exception {
        // given
        NormalSignUpRequest validData = new NormalSignUpRequest(
                "akdnjs0308@gmail.com", "userPw2!",
                "테스트닉네임", "v1.0.0", "v1.0.0", "v1.0.0");
        String testRequestBody = objectMapper.writeValueAsString(validData);

        //when
        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testRequestBody).characterEncoding("UTF-8"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists());
    }
}
