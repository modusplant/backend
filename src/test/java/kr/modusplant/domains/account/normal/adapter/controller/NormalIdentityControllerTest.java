package kr.modusplant.domains.account.normal.adapter.controller;

import kr.modusplant.domains.account.normal.adapter.mapper.NormalIdentityMapperImpl;
import kr.modusplant.domains.account.normal.common.util.domain.vo.SignUpDataTestUtils;
import kr.modusplant.domains.account.normal.common.util.usecase.request.EmailModificationRequestTestUtils;
import kr.modusplant.domains.account.normal.common.util.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.domains.account.normal.common.util.usecase.request.PasswordModificationRequestTestUtils;
import kr.modusplant.domains.account.normal.domain.exception.DataAlreadyExistsException;
import kr.modusplant.domains.account.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityCreateRepository;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityReadRepository;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.domains.account.normal.usecase.request.EmailModificationRequest;
import kr.modusplant.domains.account.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.domains.account.normal.usecase.request.PasswordModificationRequest;
import kr.modusplant.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.Password;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static kr.modusplant.domains.account.shared.kernel.common.util.AccountIdTestUtils.testNormalMemberId;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class NormalIdentityControllerTest implements EmailTestUtils,
        NormalSignUpRequestTestUtils, SignUpDataTestUtils,
        EmailModificationRequestTestUtils, PasswordModificationRequestTestUtils {
    private final NormalIdentityMapper identityMapper = Mockito.mock(NormalIdentityMapperImpl.class);
    private final NormalIdentityCreateRepository createRepository = Mockito.mock(NormalIdentityCreateRepository.class);
    private final NormalIdentityUpdateRepository updateRepository = Mockito.mock(NormalIdentityUpdateRepository.class);
    private final NormalIdentityReadRepository readRepository = Mockito.mock(NormalIdentityReadRepository.class);
    PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
    private final NormalIdentityController controller = new NormalIdentityController(
            identityMapper, createRepository, updateRepository, readRepository, encoder);

    @Test
    @DisplayName("이미 존재하는 닉네임으로 요청 시 DataAlreadyExistsException 발생")
    public void testRegisterNormalMember_givenExistingNickname_willThrowDataAlreadyExistsException() {
        // given
        NormalSignUpRequest request = testNormalSignUpRequest;

        given(readRepository.existsByNickname(Nickname.create(request.nickname()))).willReturn(true);

        // when & then
        assertThatThrownBy(() -> controller.registerNormalMember(request))
                .isInstanceOf(DataAlreadyExistsException.class);

        verify(readRepository, times(1)).existsByNickname(Nickname.create(request.nickname()));
        verify(readRepository, times(1)).existsByEmail(Email.create(request.email()));
    }

    @Test
    @DisplayName("BASIC Provider로 이미 가입된 이메일로 요청 시 ExistsEntityException 발생")
    public void testRegisterNormalMember_givenEmailWithBasicProvider_willThrowExistsEntityException() {
        // given
        NormalSignUpRequest request = testNormalSignUpRequest;
        Email requestEmail = Email.create(request.email());

        given(readRepository.existsByNickname(Nickname.create(request.nickname()))).willReturn(false);
        given(readRepository.existsByEmail(requestEmail)).willReturn(true);
        given(readRepository.getAuthProvider(requestEmail)).willReturn(AuthProvider.BASIC);

        // when & then
        assertThatThrownBy(() -> controller.registerNormalMember(request))
                .isInstanceOf(ExistsEntityException.class);

        verify(readRepository, times(1)).existsByEmail(requestEmail);
        verify(readRepository, times(1)).getAuthProvider(requestEmail);
    }

    @Test
    @DisplayName("BASIC_GOOGLE Provider로 이미 가입된 이메일로 요청 시 ExistsEntityException 발생")
    public void testRegisterNormalMember_givenEmailWithBasicGoogleProvider_willThrowExistsEntityException() {
        // given
        NormalSignUpRequest request = testNormalSignUpRequest;
        Email requestEmail = Email.create(request.email());

        given(readRepository.existsByNickname(Nickname.create(request.nickname()))).willReturn(false);
        given(readRepository.existsByEmail(requestEmail)).willReturn(true);
        given(readRepository.getAuthProvider(requestEmail)).willReturn(AuthProvider.BASIC_GOOGLE);

        // when & then
        assertThatThrownBy(() -> controller.registerNormalMember(request))
                .isInstanceOf(ExistsEntityException.class);

        verify(readRepository, times(1)).existsByEmail(requestEmail);
        verify(readRepository, times(1)).getAuthProvider(requestEmail);
    }

    @Test
    @DisplayName("BASIC_KAKAO Provider로 이미 가입된 이메일로 요청 시 ExistsEntityException 발생")
    public void testRegisterNormalMember_givenEmailWithBasicKakaoProvider_willThrowExistsEntityException() {
        // given
        NormalSignUpRequest request = testNormalSignUpRequest;
        Email requestEmail = Email.create(request.email());

        given(readRepository.existsByEmail(requestEmail)).willReturn(true);
        given(readRepository.getAuthProvider(requestEmail)).willReturn(AuthProvider.BASIC_KAKAO);

        // when & then
        assertThatThrownBy(() -> controller.registerNormalMember(request))
                .isInstanceOf(ExistsEntityException.class);

        verify(readRepository, times(1)).existsByEmail(requestEmail);
        verify(readRepository, times(1)).getAuthProvider(requestEmail);
    }

    @Test
    @DisplayName("GOOGLE Provider로 가입된 이메일로 요청 시 Google 계정으로 업데이트 진행")
    public void testRegisterNormalMember_givenEmailWithGoogleProvider_willUpdateToGoogleAccount() {
        // given
        NormalSignUpRequest request = testNormalSignUpRequest;
        Email requestEmail = Email.create(request.email());

        given(readRepository.existsByEmail(requestEmail)).willReturn(true);
        given(readRepository.getAuthProvider(requestEmail)).willReturn(AuthProvider.GOOGLE);
        doNothing().when(updateRepository).updateToGoogleAccount(requestEmail, Password.create(request.password()));

        // when
        controller.registerNormalMember(request);

        // then
        verify(updateRepository, times(1)).updateToGoogleAccount(requestEmail, Password.create(request.password()));
        verify(createRepository, never()).save(any());
    }

    @Test
    @DisplayName("KAKAO Provider로 가입된 이메일로 요청 시 Kakao 계정으로 업데이트 진행")
    public void testRegisterNormalMember_givenEmailWithKakaoProvider_willUpdateToKakaoAccount() {
        // given
        NormalSignUpRequest request = testNormalSignUpRequest;
        Email requestEmail = Email.create(request.email());

        given(readRepository.existsByEmail(requestEmail)).willReturn(true);
        given(readRepository.getAuthProvider(requestEmail)).willReturn(AuthProvider.KAKAO);
        doNothing().when(updateRepository).updateToKakaoAccount(requestEmail, Password.create(request.password()));

        // when
        controller.registerNormalMember(request);

        // then
        verify(updateRepository, times(1)).updateToKakaoAccount(requestEmail, Password.create(request.password()));
        verify(createRepository, never()).save(any());
    }

    @Test
    @DisplayName("미가입 이메일로 요청 시 신규 회원 저장 진행")
    public void testRegisterNormalMember_givenNewEmail_willSaveNewMember() {
        // given
        NormalSignUpRequest request = testNormalSignUpRequest;
        Email requestEmail = Email.create(request.email());

        given(readRepository.existsByNickname(Nickname.create(request.nickname()))).willReturn(false);
        given(readRepository.existsByEmail(requestEmail)).willReturn(false);
        doNothing().when(createRepository).save(any());

        // when
        controller.registerNormalMember(request);

        // then
        verify(createRepository, times(1)).save(identityMapper.toSignUpData(request));
        verify(updateRepository, never()).updateToGoogleAccount(any(), any());
        verify(updateRepository, never()).updateToKakaoAccount(any(), any());
    }

    @Test
    @DisplayName("유효한 요청 데이터를 받았을 시 사용자의 이메일 변경 진행")
    public void testModifyEmail_givenValidRequest_willProcessRequest() {
        // given
        EmailModificationRequest request = testEmailModificationRequest;

        given(readRepository.existsByEmail(Email.create(request.currentEmail()))).willReturn(true);
        doNothing().when(updateRepository).updateEmail(testNormalMemberId, Email.create(request.newEmail()));

        // when
        controller.modifyEmail(testNormalMemberId.getValue(), testEmailModificationRequest);

        // then
        verify(readRepository, times(1)).existsByEmail(testNormalUserEmail);
        verify(updateRepository, times(1)).updateEmail(testNormalMemberId, Email.create(request.newEmail()));
    }

    @Test
    @DisplayName("유효한 요청 데이터를 받았을 시 사용자의 비밀번호 변경 진행")
    public void testModifyPassword_givenValidRequest_willProcessRequest() {
        // given
        PasswordModificationRequest request = testPasswordModificationRequest;

        given(readRepository.existsByMemberId(testNormalMemberId)).willReturn(true);
        given(readRepository.getMemberPassword(testNormalMemberId)).willReturn(request.currentPw());
        given(encoder.matches(request.currentPw(), request.currentPw())).willReturn(true);
        doNothing().when(updateRepository).updatePassword(testNormalMemberId, Password.create(request.newPw()));

        // when
        controller.modifyPassword(testNormalMemberId.getValue(), testPasswordModificationRequest);

        // then
        verify(readRepository, times(1)).existsByMemberId(testNormalMemberId);
        verify(readRepository, times(1)).getMemberPassword(testNormalMemberId);
        verify(encoder, times(1)).matches(request.currentPw(), request.currentPw());
        verify(updateRepository, times(1)).updatePassword(testNormalMemberId, Password.create(request.newPw()));
    }
}
