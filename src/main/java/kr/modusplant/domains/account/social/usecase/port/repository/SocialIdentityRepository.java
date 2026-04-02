package kr.modusplant.domains.account.social.usecase.port.repository;


import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.vo.*;
import kr.modusplant.shared.kernel.Email;

import java.util.Optional;

public interface SocialIdentityRepository {

    Optional<SocialMemberProfile> getSocialMemberProfileByEmail(Email email);

    SocialMemberProfile updateLoggedInAtAndGetProfile(AccountId accountId);

    SocialAccountPayload createSocialMember(SocialAccountProfile profile, Role role);

}
