package kr.modusplant.domains.member.usecase.record;

import kr.modusplant.domains.member.common.util.usecase.record.MemberWithdrawalRecordTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.account.identity.common.constant.MemberAuthConstant.MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN;
import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_CODE;
import static org.assertj.core.api.Assertions.assertThat;

class MemberWithdrawalRecordTest implements MemberWithdrawalRecordTestUtils {

    @Test
    @DisplayName("인증 코드와 액세스 토큰이 있는 기록으로 String 반환")
    void testToString_givenAuthCodeAndAccessToken_willReturnString() {
        // given & when
        String result = testKakaoMemberWithdrawalRecord.toString();

        // then
        assertThat(result)
                .contains("authCode=***", "accessToken=***")
                .doesNotContain(TEST_SOCIAL_KAKAO_CODE, MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);
    }
}
