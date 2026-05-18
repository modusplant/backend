package kr.modusplant.domains.account.social.common.util.usecase.request;

import kr.modusplant.domains.account.social.usecase.request.SocialSignUpRequest;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.domains.term.common.constant.MemberTermConstant.*;

public interface SocialSignUpRequestTestUtils {
    default SocialSignUpRequest createTestSocialSignUpRequest() {
        return new SocialSignUpRequest(MEMBER_BASIC_USER_NICKNAME,"프로필 소개글",MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION,MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION,MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION);
    }
}
