package kr.modusplant.domains.member.domain.service.supers;

import kr.modusplant.domains.commons.app.service.supers.UuidCrudService;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.enums.AuthProvider;

import java.util.List;
import java.util.Optional;

public interface SiteMemberAuthCrudService extends UuidCrudService<SiteMemberAuth> {
    List<SiteMemberAuth> getByActiveMember(SiteMember activeMember);

    List<SiteMemberAuth> getByEmail(String email);

    List<SiteMemberAuth> getByProvider(AuthProvider provider);

    List<SiteMemberAuth> getByProviderId(String providerId);

    List<SiteMemberAuth> getByFailedAttempt(Integer failedAttempt);

    Optional<SiteMemberAuth> getByOriginalMember(SiteMember originalMember);

    Optional<SiteMemberAuth> getByEmailAndProvider(String email, AuthProvider provider);

    Optional<SiteMemberAuth> getByProviderAndProviderId(AuthProvider provider, String providerId);
}
