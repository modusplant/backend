package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.member.usecase.record.MemberWithdrawalRecord;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_GOOGLE_CODE;
import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_CODE;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberWithdrawConstant.MEMBER_WITHDRAW_BASIC_USER_OPINION;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberWithdrawConstant.MEMBER_WITHDRAW_BASIC_USER_REASON;

public interface MemberWithdrawalRecordTestUtils {
    MemberWithdrawalRecord testBasicMemberWithdrawalRecord =
            new MemberWithdrawalRecord(
                    null,
                    null,
                    MEMBER_WITHDRAW_BASIC_USER_REASON,
                    MEMBER_WITHDRAW_BASIC_USER_OPINION,
                    MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);

    MemberWithdrawalRecord testKakaoMemberWithdrawalRecord =
            new MemberWithdrawalRecord(
                    TEST_SOCIAL_KAKAO_CODE,
                    SocialProvider.KAKAO.getValue(),
                    MEMBER_WITHDRAW_BASIC_USER_REASON,
                    MEMBER_WITHDRAW_BASIC_USER_OPINION,
                    MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);

    MemberWithdrawalRecord testGoogleMemberWithdrawalRecord =
            new MemberWithdrawalRecord(
                    TEST_SOCIAL_GOOGLE_CODE,
                    SocialProvider.GOOGLE.getValue(),
                    MEMBER_WITHDRAW_BASIC_USER_REASON,
                    MEMBER_WITHDRAW_BASIC_USER_OPINION,
                    MEMBER_AUTH_BASIC_USER_ACCESS_TOKEN);
}
