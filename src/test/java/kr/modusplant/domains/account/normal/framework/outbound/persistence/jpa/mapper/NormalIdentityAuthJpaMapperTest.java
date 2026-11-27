package kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper;

import kr.modusplant.domains.account.identity.framework.outbound.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.account.normal.common.util.domain.vo.SignUpDataTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.shared.enums.AuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;
import static org.assertj.core.api.Assertions.assertThat;

public class NormalIdentityAuthJpaMapperTest implements MemberEntityTestUtils, SignUpDataTestUtils {
    private final NormalIdentityAuthJpaMapper mapper = new NormalIdentityAuthJpaMapper();

    @Test
    @DisplayName("유효한 회원 엔티티, 회원가입 정보, 인코딩된 비밀번호로 MemberAuthEntity 반환")
    public void testToSiteMemberAuthEntity_givenValidMemberAndSignUpData_willReturnMemberAuthEntity() {
        // given
        MemberEntity savedMember = createMemberBasicUserEntityWithUuid();

        // when
        MemberAuthEntity result = mapper.toSiteMemberAuthEntity(savedMember, TEST_NORMAL_SIGN_UP_DATA,
                MEMBER_AUTH_BASIC_USER_PW);

        // then
        assertThat(result.getMember()).isEqualTo(savedMember);
        assertThat(result.getEmail()).isEqualTo(TEST_NORMAL_SIGN_UP_DATA.getNormalCredentials().getEmail().getValue());
        assertThat(result.getPw()).isEqualTo(MEMBER_AUTH_BASIC_USER_PW);
        assertThat(result.getProvider()).isEqualTo(AuthProvider.BASIC);
    }
}
