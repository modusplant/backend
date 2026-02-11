package kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberAuthEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberTermEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_EMAIL;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

public class IdentityAuthMapperTest implements SiteMemberAuthEntityTestUtils,
        SiteMemberEntityTestUtils, SiteMemberTermEntityTestUtils {
    private final NormalIdentityAuthJpaMapper mapper = new NormalIdentityAuthJpaMapperImpl();

    @Test
    @DisplayName("유효한 사용자 식별자와 회원가입 정보를 사용자 권한 엔티티로 전환")
    public void testToSiteMemberAuthEntity_givenValidMemberUuidAndSignUpData_willReturnSiteMemberAuthEntity() {
        // given
        SiteMemberAuthEntity compare = createMemberAuthBasicUserEntityBuilder()
                .originalMember(createMemberBasicUserEntityWithUuid()).build();
        SignUpData testSign = SignUpData.create(MEMBER_AUTH_BASIC_USER_EMAIL,
                MEMBER_AUTH_BASIC_USER_PW, MEMBER_BASIC_USER_NICKNAME, MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION,
                MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION, MEMBER_TERM_ADMIN_AGREED_AD_INFO_RECEIVING_VERSION);

        // when
        SiteMemberAuthEntity result = mapper.toSiteMemberAuthEntity(createMemberBasicUserEntityWithUuid(), testSign);

        // then
        assertThat(result).isEqualTo(compare);
    }
}
