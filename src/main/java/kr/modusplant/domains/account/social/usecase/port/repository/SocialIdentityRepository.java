package kr.modusplant.domains.account.social.usecase.port.repository;


import kr.modusplant.domains.account.shared.kernel.AccountId;
import kr.modusplant.domains.account.social.domain.vo.AgreedTerms;
import kr.modusplant.domains.account.social.domain.vo.SocialCredentials;
import kr.modusplant.domains.account.social.domain.vo.SocialMemberProfile;
import kr.modusplant.shared.kernel.Email;

import java.util.Optional;

public interface SocialIdentityRepository {

    Optional<SocialMemberProfile> getSocialMemberProfileByEmail(Email email);

    SocialMemberProfile getSocialMemberProfileByAccountId(AccountId accountId);

    SocialMemberProfile updateLoggedInAtAndGetProfile(AccountId accountId);

    SocialMemberProfile saveSocialMember(SocialMemberProfile profile, String intro, AgreedTerms agreedTerms);

    SocialMemberProfile updateSocialLinkedMember(SocialCredentials socialCredentials, Email email);

    void updateSocialUnlinkedMember(AccountId accountId);
}
