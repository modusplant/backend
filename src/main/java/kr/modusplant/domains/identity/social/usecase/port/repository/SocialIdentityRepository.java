package kr.modusplant.domains.identity.social.usecase.port.repository;


import kr.modusplant.domains.identity.shared.kernel.AccountId;
import kr.modusplant.domains.identity.social.domain.vo.SocialAccountPayload;
import kr.modusplant.domains.identity.social.domain.vo.SocialAccountProfile;
import kr.modusplant.domains.identity.social.domain.vo.SocialCredentials;
import kr.modusplant.infrastructure.security.enums.Role;

import java.util.Optional;

public interface SocialIdentityRepository {

    Optional<AccountId> getMemberIdBySocialCredentials(SocialCredentials socialCredentials);

    SocialAccountPayload getUserPayloadByMemberId(AccountId accountId);

    void updateLoggedInAt(AccountId accountId);

    SocialAccountPayload createSocialMember(SocialAccountProfile profile, Role role);

}
