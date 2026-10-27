package kr.modusplant.domains.account.normal.adapter.controller;

import kr.modusplant.domains.account.normal.adapter.mapper.NormalIdentityMapperImpl;
import kr.modusplant.domains.account.normal.common.util.domain.vo.SignUpDataTestUtils;
import kr.modusplant.domains.account.normal.common.util.usecase.request.EmailModificationRequestTestUtils;
import kr.modusplant.domains.account.normal.common.util.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.domains.account.normal.common.util.usecase.request.PasswordModificationRequestTestUtils;
import kr.modusplant.domains.account.normal.domain.exception.enums.NormalIdentityErrorCode;
import kr.modusplant.domains.account.normal.usecase.port.mapper.NormalIdentityMapper;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityCreateRepository;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityReadRepository;
import kr.modusplant.domains.account.normal.usecase.port.repository.NormalIdentityUpdateRepository;
import kr.modusplant.domains.account.normal.usecase.request.EmailModificationRequest;
import kr.modusplant.domains.account.normal.usecase.request.NormalSignUpRequest;
import kr.modusplant.domains.account.normal.usecase.request.PasswordModificationRequest;
import kr.modusplant.shared.enums.AuthProvider;
import kr.modusplant.shared.exception.ExistsValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import kr.modusplant.shared.framework.jdbc.common.util.SwearHolderTestUtils;
import kr.modusplant.shared.framework.jdbc.holder.SwearHolder;
import kr.modusplant.shared.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.kernel.Email;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.Password;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;
import static kr.modusplant.domains.account.shared.kernel.common.util.AccountIdTestUtils.testNormalMemberId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class NormalIdentityControllerTest implements EmailTestUtils,
        NormalSignUpRequestTestUtils, SignUpDataTestUtils,
        EmailModificationRequestTestUtils, PasswordModificationRequestTestUtils {
    @SuppressWarnings("unused")
    private final SwearHolder swearHolder = SwearHolderTestUtils.createSwearHolder();

    private final NormalIdentityMapper identityMapper = Mockito.mock(NormalIdentityMapperImpl.class);
    private final NormalIdentityCreateRepository createRepository = Mockito.mock(NormalIdentityCreateRepository.class);
    private final NormalIdentityUpdateRepository updateRepository = Mockito.mock(NormalIdentityUpdateRepository.class);
    private final NormalIdentityReadRepository readRepository = Mockito.mock(NormalIdentityReadRepository.class);
    PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
    private final NormalIdentityController controller = new NormalIdentityController(
            identityMapper, createRepository, updateRepository, readRepository, encoder);

    @Test
    @DisplayName("이미 존재하는 닉네임으로 요청 시 ExistsValueException 발생")
    public void testRegisterNormalMember_givenExistingNickname_willThrowException() {
        // given
        NormalSignUpRequest request = testNormalSignUpRequest;

        given(readRepository.existsByNickname(Nickname.create(request.nickname()))).willReturn(true);

        // when & then
        assertThatThrownBy(() -> controller.registerNormalMember(request))
                .isInstanceOf(ExistsValueException.class)
                .extracting("errorCode").isEqualTo(KernelErrorCode.EXISTS_NICKNAME);

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

        given(readRepository.getMemberEmail(testNormalMemberId)).willReturn(testNormalUserEmail);
        given(readRepository.getAuthProvider(testNormalMemberId)).willReturn(AuthProvider.BASIC);
        doNothing().when(updateRepository).updateEmail(testNormalMemberId, Email.create(request.newEmail()));

        // when
        controller.modifyEmail(testNormalMemberId.getValue(), testEmailModificationRequest);

        // then
        verify(readRepository, times(1)).getMemberEmail(testNormalMemberId);
        verify(updateRepository, times(1)).updateEmail(testNormalMemberId, Email.create(request.newEmail()));
    }

    @Test
    @DisplayName("존재하지 않는 회원의 이메일 변경 시 예외 반환")
    public void testModifyEmail_givenNotFoundMember_willThrowException() {
        // given
        given(readRepository.getMemberEmail(testNormalMemberId)).willReturn(null);

        // when & then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class,
                () -> controller.modifyEmail(testNormalMemberId.getValue(), testEmailModificationRequest));

        assertThat(exception.getErrorCode()).isEqualTo(NormalIdentityErrorCode.NOT_FOUND_ACCOUNT);
        assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        verify(updateRepository, never()).updateEmail(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 회원의 비밀번호 변경 시 예외 반환")
    public void testModifyPassword_givenNotFoundMember_willThrowException() {
        // given
        given(readRepository.existsByMemberId(testNormalMemberId)).willReturn(false);

        // when & then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class,
                () -> controller.modifyPassword(testNormalMemberId.getValue(), testPasswordModificationRequest));

        assertThat(exception.getErrorCode()).isEqualTo(NormalIdentityErrorCode.NOT_FOUND_ACCOUNT);
        assertThat(exception.getErrorCode().getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        verify(updateRepository, never()).updatePassword(any(), any());
    }

    @Test
    @DisplayName("일반 회원이 아닌 회원의 이메일 변경 시 예외 반환")
    public void testModifyEmail_givenNonBasicAuthProvider_willThrowException() {
        // given
        given(readRepository.getMemberEmail(testNormalMemberId)).willReturn(testNormalUserEmail);
        given(readRepository.getAuthProvider(testNormalMemberId)).willReturn(AuthProvider.KAKAO);

        // when & then
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> controller.modifyEmail(testNormalMemberId.getValue(), testEmailModificationRequest));

        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.MISMATCH_INPUT_TYPE);
        verify(updateRepository, never()).updateEmail(any(), any());
    }

    @Test
    @DisplayName("회원의 이메일과 다른 현재 이메일로 이메일 변경 시 예외 반환")
    public void testModifyEmail_givenMismatchingCurrentEmail_willThrowException() {
        // given
        given(readRepository.getMemberEmail(testNormalMemberId)).willReturn(testKakaoUserEmail);
        given(readRepository.getAuthProvider(testNormalMemberId)).willReturn(AuthProvider.BASIC);

        // when & then
        InvalidValueException exception = assertThrows(InvalidValueException.class,
                () -> controller.modifyEmail(testNormalMemberId.getValue(), testEmailModificationRequest));

        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.INVALID_INPUT);
        verify(updateRepository, never()).updateEmail(any(), any());
    }

    @Test
    @DisplayName("유효한 요청 데이터를 받았을 시 사용자의 비밀번호 변경 진행")
    public void testModifyPassword_givenValidRequest_willProcessRequest() {
        // given
        PasswordModificationRequest request = testPasswordModificationRequest;

        given(readRepository.existsByMemberId(testNormalMemberId)).willReturn(true);
        given(readRepository.getMemberPassword(testNormalMemberId)).willReturn(MEMBER_AUTH_BASIC_USER_PW);
        given(encoder.matches(request.currentPw(), MEMBER_AUTH_BASIC_USER_PW)).willReturn(true);
        doNothing().when(updateRepository).updatePassword(testNormalMemberId, Password.create(request.newPw()));

        // when
        controller.modifyPassword(testNormalMemberId.getValue(), testPasswordModificationRequest);

        // then
        verify(readRepository, times(1)).existsByMemberId(testNormalMemberId);
        verify(readRepository, times(1)).getMemberPassword(testNormalMemberId);
        verify(encoder, times(1)).matches(request.currentPw(), MEMBER_AUTH_BASIC_USER_PW);
        verify(updateRepository, times(1)).updatePassword(testNormalMemberId, Password.create(request.newPw()));
    }
}
