package kr.modusplant.modules.auth.normal.signup.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberAuthApplicationService;
import kr.modusplant.domains.member.app.service.SiteMemberTermApplicationService;
import kr.modusplant.domains.member.common.util.app.http.response.SiteMemberAuthResponseTestUtils;
import kr.modusplant.domains.member.common.util.app.http.response.SiteMemberResponseTestUtils;
import kr.modusplant.domains.member.common.util.app.http.response.SiteMemberTermResponseTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.term.app.service.TermApplicationService;
import kr.modusplant.domains.term.common.util.app.http.response.TermResponseTestUtils;
import kr.modusplant.modules.auth.normal.signup.app.http.request.NormalSignUpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class NormalSignUpControllerUnitTest implements SiteMemberResponseTestUtils, SiteMemberAuthResponseTestUtils, SiteMemberTermResponseTestUtils {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private TermApplicationService termApplicationService;
    @MockitoBean
    private SiteMemberApplicationService siteMemberApplicationService;
    @MockitoBean
    private SiteMemberAuthApplicationService siteMemberAuthApplicationService;
    @MockitoBean
    private SiteMemberTermApplicationService siteMemberTermApplicationService;

    @Test
    public void sendTerms_givenRequestingTerm_thenReturn200WithTerm() throws Exception {
        // given
        given(termApplicationService.getAll())
                .willReturn(List.of(TermResponseTestUtils.termsOfUseResponse));

        // when
        mockMvc.perform(get("/api/terms"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[*].termsOfUse").exists());

    }

    @Test
    public void saveMember_givenValidInput_thenReturn200() throws Exception {
        // given
        NormalSignUpRequest validData = new NormalSignUpRequest(
                "akdnjs0308@gmail.com", "userPw2!",
                "테스트닉네임", "v1.0.0", "v1.0.0", "v1.0.0");
        String testRequestBody = objectMapper.writeValueAsString(validData);
        setupServiceStubbing();

        //when
        mockMvc.perform(post("/api/members/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testRequestBody).characterEncoding("UTF-8"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists());
    }

    private void setupServiceStubbing() {
        UUID consistentMemberUuid = SiteMemberTestUtils.memberBasicUserWithUuid.getUuid();

        given(siteMemberApplicationService
                .insert(argThat(member ->
                        member != null &&
                                member.nickname() != null)))
                .willReturn(memberBasicUserResponse);

        given(siteMemberAuthApplicationService
                .insert(argThat(auth ->
                        auth != null &&
                                auth.originalMemberUuid().equals(consistentMemberUuid) &&
                                auth.provider().equals(AuthProvider.BASIC))))
                .willReturn(memberAuthBasicUserResponse);

        given(siteMemberTermApplicationService
                .insert(argThat(memberTerm ->
                        memberTerm != null &&
                                memberTerm.uuid().equals(consistentMemberUuid))))
                .willReturn(memberTermUserResponse);
    }
}
