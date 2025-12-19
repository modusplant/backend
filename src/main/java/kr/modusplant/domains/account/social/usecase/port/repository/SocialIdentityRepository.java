package kr.modusplant.domains.account.social.usecase.port.repository;


import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.vo.SocialAccountPayload;
import kr.modusplant.domains.account.social.domain.vo.SocialAccountProfile;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.infrastructure.security.enums.Role;

import java.util.Optional;

public interface SocialIdentityRepository {

    Optional<AccountId> getMemberIdBySocialCredentials(SocialCredentials socialCredentials);

    SocialAccountPayload getUserPayloadByMemberId(AccountId accountId);

    void updateLoggedInAt(AccountId accountId);

    SocialAccountPayload createSocialMember(SocialAccountProfile profile, Role role);

}
