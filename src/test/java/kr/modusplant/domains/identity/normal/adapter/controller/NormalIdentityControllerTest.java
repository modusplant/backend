package kr.modusplant.domains.identity.normal.adapter.controller;

import kr.modusplant.domains.identity.normal.adapter.mapper.NormalIdentityMapperImpl;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.SignUpDataTestUtils;
import kr.modusplant.domains.identity.normal.common.util.usecase.request.EmailModificationRequestTestUtils;
import kr.modusplant.domains.identity.normal.common.util.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.domains.identity.normal.common.util.usecase.request.PasswordModificationRequestTestUtils;
import kr.modusplant.domains.identity.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityCreateRepository;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityReadRepository;
import kr.modusplant.domains.identity.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.domains.identity.normal.usecase.request.EmailModificationRequest;
import kr.modusplant.domains.identity.normal.usecase.request.PasswordModificationRequest;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Password;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class NormalIdentityControllerTest implements
        NormalSignUpRequestTestUtils, SignUpDataTestUtils, MemberIdTestUtils,
        EmailModificationRequestTestUtils, PasswordModificationRequestTestUtils {
    private final NormalIdentityMapper mapper = Mockito.mock(NormalIdentityMapperImpl.class);
    private final NormalIdentityCreateRepository createRepository = Mockito.mock(NormalIdentityCreateRepository.class);
    private final NormalIdentityUpdateRepository updateRepository = Mockito.mock(NormalIdentityUpdateRepository.class);
    private final NormalIdentityReadRepository readRepository = Mockito.mock(NormalIdentityReadRepository.class);
    private final PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
    private final NormalIdentityController controller = new NormalIdentityController(
            mapper, createRepository, updateRepository, readRepository, encoder);

    @Test
    @DisplayName("유효한 요청 데이터를 받았을 시 일반 회원가입 진행")
    public void testRegisterNormalMember_givenValidRequest_willProcessRequest() {
        // given
        given(readRepository.existsByEmail(testEmail)).willReturn(false);
        given(readRepository.existsByNickname(testNormalUserNickname)).willReturn(false);
        given(mapper.toSignUpData(testNormalSignUpRequest)).willReturn(TEST_NORMAL_SIGN_UP_DATA);
        doNothing().when(createRepository).save(TEST_NORMAL_SIGN_UP_DATA);

        // when
        controller.registerNormalMember(testNormalSignUpRequest);

        // then
        verify(readRepository, times(1)).existsByEmail(testEmail);
        verify(readRepository, times(1)).existsByNickname(testNormalUserNickname);
        verify(mapper, times(1)).toSignUpData(testNormalSignUpRequest);
        verify(createRepository, times(1)).save(TEST_NORMAL_SIGN_UP_DATA);
    }

    @Test
    @DisplayName("유효한 요청 데이터를 받았을 시 사용자의 이메일 변경 진행")
    public void testModifyEmail_givenValidRequest_willProcessRequest() {
        // given
        EmailModificationRequest request = testEmailModificationRequest;

        given(readRepository.existsByEmail(Email.create(request.currentEmail()))).willReturn(true);
        given(updateRepository.updateEmail(TEST_NORMAL_MEMBER_ID, Email.create(request.newEmail()))).willReturn(1);

        // when
        controller.modifyEmail(TEST_NORMAL_MEMBER_ID.getValue(), testEmailModificationRequest);

        // then
        verify(readRepository, times(1)).existsByEmail(testEmail);
        verify(updateRepository, times(1)).updateEmail(TEST_NORMAL_MEMBER_ID, Email.create(request.newEmail()));
    }

    @Test
    @DisplayName("유효한 요청 데이터를 받았을 시 사용자의 비밀번호 변경 진행")
    public void testModifyPassword_givenValidRequest_willProcessRequest() {
        // given
        PasswordModificationRequest request = testPasswordModificationRequest;

        given(readRepository.existsByMemberId(TEST_NORMAL_MEMBER_ID)).willReturn(true);
        given(readRepository.getMemberPassword(TEST_NORMAL_MEMBER_ID)).willReturn(request.currentPw());
        given(encoder.matches(request.currentPw(), request.currentPw())).willReturn(true);
        given(updateRepository.updatePassword(TEST_NORMAL_MEMBER_ID, Password.create(request.newPw())))
                .willReturn(1);

        // when
        controller.modifyPassword(TEST_NORMAL_MEMBER_ID.getValue(), testPasswordModificationRequest);

        // then
        verify(readRepository, times(1)).existsByMemberId(TEST_NORMAL_MEMBER_ID);
        verify(readRepository, times(1)).getMemberPassword(TEST_NORMAL_MEMBER_ID);
        verify(encoder, times(1)).matches(request.currentPw(), request.currentPw());
        verify(updateRepository, times(1)).updatePassword(TEST_NORMAL_MEMBER_ID, Password.create(request.newPw()));
    }
}
