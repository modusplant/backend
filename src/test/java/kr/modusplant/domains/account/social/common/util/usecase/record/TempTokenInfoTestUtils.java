package kr.modusplant.domains.account.social.common.util.usecase.record;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.record.TempTokenInfo;

import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_GOOGLE_PROVIDER_ID_STRING;
import static kr.modusplant.domains.account.social.common.constant.SocialStringConstant.TEST_SOCIAL_KAKAO_PROVIDER_ID_STRING;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.*;

public interface TempTokenInfoTestUtils {
    default TempTokenInfo createKakaoTempTokenInfo() {
        return new TempTokenInfo(MEMBER_AUTH_KAKAO_USER_EMAIL,TEST_SOCIAL_KAKAO_PROVIDER_ID_STRING, SocialProvider.KAKAO);
    }

    default TempTokenInfo createGoogleTempTokenInfo() {
        return new TempTokenInfo(MEMBER_AUTH_GOOGLE_USER_EMAIL,TEST_SOCIAL_GOOGLE_PROVIDER_ID_STRING, SocialProvider.GOOGLE);
    }

    default TempTokenInfo createKakaoTempTokenInfoWithBasicEmail() {
        return new TempTokenInfo(MEMBER_AUTH_BASIC_USER_EMAIL,TEST_SOCIAL_KAKAO_PROVIDER_ID_STRING, SocialProvider.KAKAO);
    }

    default TempTokenInfo createGoogleTempTokenInfoWithBasicEmail() {
        return new TempTokenInfo(MEMBER_AUTH_BASIC_USER_EMAIL,TEST_SOCIAL_GOOGLE_PROVIDER_ID_STRING, SocialProvider.GOOGLE);
    }
}
