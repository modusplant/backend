package kr.modusplant.domains.identity.adapter.mapper;

import kr.modusplant.domains.identity.common.utils.domain.vo.SignUpDataTestUtils;
import kr.modusplant.domains.identity.common.utils.usecase.request.NormalSignUpRequestTestUtils;
import kr.modusplant.domains.identity.domain.vo.SignUpData;
import kr.modusplant.domains.identity.usecase.port.mapper.NormalIdentityMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class NormalIdentityMapperImplTest implements
        NormalSignUpRequestTestUtils, SignUpDataTestUtils {

    private final NormalIdentityMapper mapper = new NormalIdentityMapperImpl();
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    public void tesToSignUpData_givenValidRequest_willReturnSignUpData() {
        // given & when
        SignUpData result = mapper.toSignUpData(testNormalSignUpRequest);

        // then
        assertThat(result.getCredentials().getEmail())
                .isEqualTo(testSignUpData.getCredentials().getEmail());
        assertThat(encoder.matches(testCredentials.getPassword(), result.getCredentials().getPassword()))
                .isTrue();
        assertThat(result.getNickname().getNickname())
                .isEqualTo(testSignUpData.getNickname().getNickname());
        assertThat(result.getAgreedTermsOfUseVersion().getVersion())
                .isEqualTo(testSignUpData.getAgreedTermsOfUseVersion().getVersion());
        assertThat(result.getAgreedPrivacyPolicyVersion().getVersion())
                .isEqualTo(testSignUpData.getAgreedPrivacyPolicyVersion().getVersion());
        assertThat(result.getAgreedAdInfoReceivingVersion().getVersion())
                .isEqualTo(testSignUpData.getAgreedAdInfoReceivingVersion().getVersion());
    }
}
