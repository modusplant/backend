package kr.modusplant.domains.account.social.common.util.domain.vo;

import kr.modusplant.domains.account.shared.kernel.common.util.AccountIdTestUtils;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.kernel.common.util.EmailTestUtils;
import kr.modusplant.shared.kernel.common.util.NicknameTestUtils;

public interface SocialMemberProfileTestUtils extends AccountIdTestUtils, SocialCredentialsTestUtils, EmailTestUtils, NicknameTestUtils {
    SocialMemberProfile testBasicSocialMemberProfile = SocialMemberProfile.create(testNormalMemberId,testBasicSocialCredentials,testNormalUserEmail,testNormalUserNickname, Role.USER);
    SocialMemberProfile testKakaoSocialMemberProfile = SocialMemberProfile.create(testKakaoAccountId,testKakaoSocialCredentials,testKakaoUserEmail,testKakaoUserNickname, Role.USER);
    SocialMemberProfile testGoogleSocialMemberProfile = SocialMemberProfile.create(testGoogleAccountId,testGoogleSocialCredentials,testGoogleUserEmail,testGoogleUserNickname, Role.USER);
    SocialMemberProfile testKakaoSocialMemberProfileWithBasicEmail = SocialMemberProfile.create(testKakaoAccountId,testKakaoSocialCredentials,testNormalUserEmail,testNormalUserNickname, Role.USER);
    SocialMemberProfile testGoogleSocialMemberProfileWithBasicEmail = SocialMemberProfile.create(testGoogleAccountId,testGoogleSocialCredentials,testNormalUserEmail,testNormalUserNickname, Role.USER);
    SocialMemberProfile testBasicKakaoSocialMemberProfile = SocialMemberProfile.create(testNormalMemberId,testBasicKakaoSocialCredentials,testNormalUserEmail,testNormalUserNickname, Role.USER);
    SocialMemberProfile testBasicGoogleSocialMemberProfile = SocialMemberProfile.create(testNormalMemberId,testBasicGoogleSocialCredentials,testNormalUserEmail,testNormalUserNickname, Role.USER);
}
