package kr.modusplant.domains.member.common.util.usecase.request;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.member.usecase.request.MemberWithdrawRequest;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_GOOGLE_CODE;
import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_CODE;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberWithdrawConstant.MEMBER_WITHDRAW_BASIC_USER_OPINION;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberWithdrawConstant.MEMBER_WITHDRAW_BASIC_USER_REASON;

public interface MemberWithdrawRequestTestUtils {
    MemberWithdrawRequest testBasicMemberWithdrawRequest =
            new MemberWithdrawRequest(
                    null,
                    null,
                    MEMBER_WITHDRAW_BASIC_USER_REASON,
                    MEMBER_WITHDRAW_BASIC_USER_OPINION
            );

    MemberWithdrawRequest testKakaoMemberWithdrawRequest =
            new MemberWithdrawRequest(
                    TEST_SOCIAL_KAKAO_CODE,
                    SocialProvider.KAKAO.getValue(),
                    MEMBER_WITHDRAW_BASIC_USER_REASON,
                    MEMBER_WITHDRAW_BASIC_USER_OPINION
            );

    MemberWithdrawRequest testGoogleMemberWithdrawRequest =
            new MemberWithdrawRequest(
                    TEST_SOCIAL_GOOGLE_CODE,
                    SocialProvider.GOOGLE.getValue(),
                    MEMBER_WITHDRAW_BASIC_USER_REASON,
                    MEMBER_WITHDRAW_BASIC_USER_OPINION
            );
}
