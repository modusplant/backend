package kr.modusplant.domains.identity.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.identity.normal.domain.vo.SignUpData;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_EMAIL;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberTermConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

public class IdentityJpaMapperTest {
    private final IdentityJpaMapper mapper = new IdentityJpaMapperImpl();

    @Test
    @DisplayName("유효한 회원가입 정보를 사용자 권한 엔티티로 전환")
    public void testToSiteMemberEntity_givenValidMemberUuidAndSignUpData_willReturnSiteMemberAuthEntity() {
        // given
        SiteMemberEntity compare = SiteMemberEntity.builder()
                .nickname(MEMBER_BASIC_USER_NICKNAME).build();
        SignUpData testSign = SignUpData.create(MEMBER_AUTH_BASIC_USER_EMAIL,
                MEMBER_AUTH_BASIC_USER_PW, MEMBER_BASIC_USER_NICKNAME, MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION,
                MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION, MEMBER_TERM_ADMIN_AGREED_AD_INFO_RECEIVING_VERSION);

        // when
        SiteMemberEntity result = mapper.toSiteMemberEntity(testSign.getNickname());

        // then
        assertThat(result).isEqualTo(compare);
    }
}
