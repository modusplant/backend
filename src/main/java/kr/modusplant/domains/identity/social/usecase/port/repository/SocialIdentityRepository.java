package kr.modusplant.domains.identity.social.usecase.port.repository;


import kr.modusplant.domains.identity.social.domain.vo.*;
import kr.modusplant.infrastructure.security.enums.Role;

import java.util.Optional;

public interface SocialIdentityRepository {

    Optional<MemberId> getMemberIdBySocialCredentials(SocialCredentials socialCredentials);

    UserPayload getUserPayloadByMemberId(MemberId memberId);

    void updateLoggedInAt(MemberId memberId);

    UserPayload createSocialMember(SocialUserProfile profile, Role role);

}
