package kr.modusplant.domains.identity.normal.adapter.mapper;

import kr.modusplant.domains.identity.normal.common.util.domain.vo.NormalCredentialsTestUtils;
import kr.modusplant.domains.identity.normal.common.util.domain.vo.SignUpDataTestUtils;
import kr.modusplant.domains.identity.normal.common.util.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;
import kr.modusplant.domains.identity.normal.usecase.port.mapper.NormalIdentityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class NormalIdentityMapperImplTest implements NormalCredentialsTestUtils,
        NormalSignUpRequestTestUtils, SignUpDataTestUtils {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final NormalIdentityMapper mapper = new NormalIdentityMapperImpl(encoder);

    @Test
    @DisplayName("유효한 요청을 일반 회원가입 데이터 VO로 변환")
    public void tesToSignUpData_givenValidRequest_willReturnSignUpData() {
        // given & when
        SignUpData result = mapper.toSignUpData(testNormalSignUpRequest);

        // then
        assertThat(result.getNormalCredentials().getEmail())
                .isEqualTo(TEST_NORMAL_SIGN_UP_DATA.getNormalCredentials().getEmail());
        assertThat(encoder.matches(testNormalCredentials.getPassword().getValue(),
                result.getNormalCredentials().getPassword().getValue())).isTrue();
        assertThat(result.getNickname().getValue())
                .isEqualTo(TEST_NORMAL_SIGN_UP_DATA.getNickname().getValue());
        assertThat(result.getAgreedTermsOfUseVersion().getValue())
                .isEqualTo(TEST_NORMAL_SIGN_UP_DATA.getAgreedTermsOfUseVersion().getValue());
        assertThat(result.getAgreedPrivacyPolicyVersion().getValue())
                .isEqualTo(TEST_NORMAL_SIGN_UP_DATA.getAgreedPrivacyPolicyVersion().getValue());
        assertThat(result.getAgreedAdInfoReceivingVersion().getValue())
                .isEqualTo(TEST_NORMAL_SIGN_UP_DATA.getAgreedAdInfoReceivingVersion().getValue());
    }
}
