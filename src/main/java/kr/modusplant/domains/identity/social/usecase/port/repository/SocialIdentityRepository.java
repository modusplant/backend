package kr.modusplant.domains.identity.social.usecase.port.repository;


import kr.modusplant.domains.identity.social.domain.vo.MemberId;
import kr.modusplant.domains.identity.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.identity.social.domain.vo.SocialAccountProfile;
import kr.modusplant.domains.identity.social.domain.vo.SocialAccountPayload;
import kr.modusplant.infrastructure.security.enums.Role;

import java.util.Optional;

public interface SocialIdentityRepository {

    Optional<MemberId> getMemberIdBySocialCredentials(SocialCredentials socialCredentials);

    SocialAccountPayload getUserPayloadByMemberId(MemberId memberId);

    void updateLoggedInAt(MemberId memberId);

    SocialAccountPayload createSocialMember(SocialAccountProfile profile, Role role);

}
