package kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper;

import kr.modusplant.domains.account.identity.framework.outbound.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.account.identity.framework.outbound.jpa.entity.common.util.MemberAuthEntityTestUtils;
import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.domains.term.framework.outbound.jpa.entity.common.util.MemberTermEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_EMAIL;
import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.term.common.constant.MemberTermConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

public class IdentityAuthMapperTest implements MemberAuthEntityTestUtils,
        MemberEntityTestUtils, MemberTermEntityTestUtils {
    private final NormalIdentityAuthJpaMapper mapper = new NormalIdentityAuthJpaMapperImpl();

    @Test
    @DisplayName("유효한 사용자 식별자와 회원가입 정보를 사용자 권한 엔티티로 전환")
    public void testToSiteMemberAuthEntity_givenValidMemberUuidAndSignUpData_willReturnSiteMemberAuthEntity() {
        // given
        MemberAuthEntity compare = createMemberAuthBasicUserEntityBuilder()
                .member(createMemberBasicUserEntityWithUuid()).build();
        SignUpData testSign = SignUpData.create(MEMBER_AUTH_BASIC_USER_EMAIL,
                MEMBER_AUTH_BASIC_USER_PW, MEMBER_BASIC_USER_NICKNAME, MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION,
                MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION, MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION);

        // when
        MemberAuthEntity result = mapper.toSiteMemberAuthEntity(createMemberBasicUserEntityWithUuid(), testSign);

        // then
        assertThat(result).isEqualTo(compare);
    }
}
