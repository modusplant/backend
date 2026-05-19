package kr.modusplant.domains.account.normal.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.account.normal.domain.vo.SignUpData;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_EMAIL;
import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_PW;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.term.common.constant.MemberTermConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

public class NormalIdentityJpaMapperTest {
    private final NormalIdentityJpaMapper mapper = new NormalIdentityJpaMapperImpl();

    @Test
    @DisplayName("유효한 사용자 닉네임을 사용자 엔티티로 전환")
    public void testToSiteMemberEntity_givenValidNickname_willReturnMemberEntity() {
        // given
        MemberEntity compare = MemberEntity.builder()
                .nickname(MEMBER_BASIC_USER_NICKNAME).build();
        SignUpData testSign = SignUpData.create(MEMBER_AUTH_BASIC_USER_EMAIL,
                MEMBER_AUTH_BASIC_USER_PW, MEMBER_BASIC_USER_NICKNAME, MEMBER_TERM_ADMIN_AGREED_TERMS_OF_USE_VERSION,
                MEMBER_TERM_ADMIN_AGREED_PRIVACY_POLICY_VERSION, MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION);

        // when
        MemberEntity result = mapper.toMemberEntity(testSign.getNickname());

        // then
        assertThat(result).isEqualTo(compare);
    }
}
