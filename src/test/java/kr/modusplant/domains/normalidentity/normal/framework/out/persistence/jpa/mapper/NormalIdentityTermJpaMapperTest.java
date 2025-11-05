package kr.modusplant.domains.normalidentity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.normalidentity.normal.domain.vo.SignUpData;
import kr.modusplant.framework.out.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_EMAIL;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

public class NormalIdentityTermJpaMapperTest implements SiteMemberEntityTestUtils {
    private final NormalIdentityTermJpaMapper mapper = new NormalIdentityTermJpaMapperImpl();

    @Test
    @DisplayName("유효한 사용자 엔티티와 회원가입 정보를 사용자 약관 엔티티로 전환")
    public void testToSiteMemberTermEntity_givenValidSiteMemberEntityAndSignUpData_willReturnSiteMemberTermEntity() {
        // given
        SignUpData testSign = SignUpData.create(MEMBER_AUTH_BASIC_USER_EMAIL,
                MEMBER_AUTH_BASIC_USER_PW, MEMBER_BASIC_USER_NICKNAME, MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION,
                MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION, MEMBER_TERM_ADMIN_AGREED_AD_INFO_RECEIVING_VERSION);

        // when
        SiteMemberTermEntity result = mapper.toSiteMemberTermEntity(createMemberBasicUserEntityWithUuid(), testSign);

        // then
        assertThat(result.getMember()).isEqualTo(createMemberBasicUserEntityWithUuid());
        assertThat(result.getAgreedPrivacyPolicyVersion()).isEqualTo(testSign.getAgreedPrivacyPolicyVersion().getVersion());
        assertThat(result.getAgreedAdInfoReceivingVersion()).isEqualTo(testSign.getAgreedAdInfoReceivingVersion().getVersion());
        assertThat(result.getAgreedTermsOfUseVersion()).isEqualTo(testSign.getAgreedTermsOfUseVersion().getVersion());
    }
}
