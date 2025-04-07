package kr.modusplant.api.crud.member.domain.service;

import kr.modusplant.api.crud.common.domain.supers.UuidCrudService;
import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.domain.model.SiteMemberAuth;
import kr.modusplant.api.crud.member.enums.AuthProvider;

import java.util.List;
import java.util.Optional;

public interface SiteMemberAuthService extends UuidCrudService<SiteMemberAuth> {
    List<SiteMemberAuth> getByActiveMember(SiteMember activeMember);

    List<SiteMemberAuth> getByEmail(String email);

    List<SiteMemberAuth> getByProvider(AuthProvider provider);

    List<SiteMemberAuth> getByProviderId(String providerId);

    List<SiteMemberAuth> getByFailedAttempt(Integer failedAttempt);

    Optional<SiteMemberAuth> getByOriginalMember(SiteMember originalMember);

    Optional<SiteMemberAuth> getByEmailAndProvider(String email, AuthProvider provider);

    Optional<SiteMemberAuth> getByProviderAndProviderId(AuthProvider provider, String providerId);
}
