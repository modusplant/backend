package kr.modusplant.modules.signup.normal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.member.common.util.domain.SiteMemberAuthTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTermTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberAuthCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberTermCrudService;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.term.common.util.domain.TermTestUtils;
import kr.modusplant.domains.term.domain.service.supers.TermCrudService;
import kr.modusplant.modules.signup.normal.model.request.NormalSignUpRequest;
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
public class NormalSignUpControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private TermCrudService termCrudService;
    @MockitoBean
    private SiteMemberCrudService siteMemberCrudService;
    @MockitoBean
    private SiteMemberAuthCrudService siteMemberAuthCrudService;
    @MockitoBean
    private SiteMemberTermCrudService siteMemberTermCrudService;

    @Test
    public void sendTerms_givenRequestingTerm_thenReturn200WithTerm() throws Exception {
        // given
        given(termCrudService.getAll())
                .willReturn(List.of(TermTestUtils.termsOfUseWithUuid, TermTestUtils.privacyPolicyWithUuid, TermTestUtils.adInfoReceivingWithUuid));

        // when
        mockMvc.perform(get("/api/terms"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[*].termsOfUse").exists())
                .andExpect(jsonPath("$.data[*].privacyPolicy").exists())
                .andExpect(jsonPath("$.data[*].adInfoReceiving").exists());
    }

    @Test
    public void saveMember_givenValidInput_thenReturn200() throws Exception {
        // given
        NormalSignUpRequest validData = new NormalSignUpRequest(
                "akdnjs0308@gmail.com", "userPw2!", "userPw2!",
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

    @Test
    public void saveMember_givenInvalidInput_thenReturn400() throws Exception {
        // given
        NormalSignUpRequest invalidData = new NormalSignUpRequest(
                "akdnjs0308@gmail.com", "userPw2!", "wrongPw",
                "테스트닉네임", "v1.0.0", "v1.0.0", "v1.0.0");
        String testRequestBody = objectMapper.writeValueAsString(invalidData);
        setupServiceStubbing();

        // when
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testRequestBody).characterEncoding("UTF-8")
                )

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    private void setupServiceStubbing() {
        UUID consistentMemberUuid = SiteMemberTestUtils.memberBasicUserWithUuid.getUuid();

        given(siteMemberCrudService
                .insert(argThat(member ->
                        member != null &&
                                member.getNickname() != null)))
                .willReturn(SiteMemberTestUtils.memberBasicUserWithUuid);

        given(siteMemberAuthCrudService
                .insert(argThat(auth ->
                    auth != null &&
                            auth.getActiveMemberUuid().equals(consistentMemberUuid) &&
                            auth.getOriginalMemberUuid().equals(consistentMemberUuid) &&
                            auth.getProvider().equals(AuthProvider.BASIC))))
                .willReturn(SiteMemberAuthTestUtils.memberAuthBasicUserWithUuid);

        given(siteMemberTermCrudService
                .insert(argThat(memberTerm ->
                    memberTerm != null &&
                            memberTerm.getUuid().equals(consistentMemberUuid))))
                .willReturn(SiteMemberTermTestUtils.memberTermUserWithUuid);
    }
}
