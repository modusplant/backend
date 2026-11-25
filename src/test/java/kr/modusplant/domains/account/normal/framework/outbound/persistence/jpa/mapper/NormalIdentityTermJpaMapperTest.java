package kr.modusplant.domains.account.normal.framework.outbound.persistence.jpa.mapper;

import kr.modusplant.domains.account.normal.common.util.domain.vo.SignUpDataTestUtils;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.term.framework.outbound.jpa.entity.MemberTermEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NormalIdentityTermJpaMapperTest implements MemberEntityTestUtils, SignUpDataTestUtils {
    private final NormalIdentityTermJpaMapper mapper = new NormalIdentityTermJpaMapper();

    @Test
    @DisplayName("유효한 회원 엔티티와 회원가입 정보로 MemberTermEntity 반환")
    public void testToSiteMemberTermEntity_givenValidMemberAndSignUpData_willReturnMemberTermEntity() {
        // given
        MemberEntity savedMember = createMemberBasicUserEntityWithUuid();

        // when
        MemberTermEntity result = mapper.toSiteMemberTermEntity(savedMember, TEST_NORMAL_SIGN_UP_DATA);

        // then
        assertThat(result.getMember()).isEqualTo(savedMember);
        assertThat(result.getAgreedTermsOfUseVersion())
                .isEqualTo(TEST_NORMAL_SIGN_UP_DATA.getAgreedTermsOfUseVersion().getValue());
        assertThat(result.getAgreedPrivacyPolicyVersion())
                .isEqualTo(TEST_NORMAL_SIGN_UP_DATA.getAgreedPrivacyPolicyVersion().getValue());
        assertThat(result.getAgreedCommunityPolicyVersion())
                .isEqualTo(TEST_NORMAL_SIGN_UP_DATA.getAgreedCommunityPolicyVersion().getValue());
    }
}
