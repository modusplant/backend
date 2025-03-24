package kr.modusplant.global.domain.service.crud;

import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.model.SiteMemberAuth;
import kr.modusplant.global.domain.service.crud.supers.UuidCrudService;
import kr.modusplant.global.enums.AuthProvider;

import java.util.List;
import java.util.Optional;

public interface SiteMemberAuthService extends UuidCrudService<SiteMemberAuth> {
    List<SiteMemberAuth> getByActiveMember(SiteMember activeMember);

    List<SiteMemberAuth> getByEmail(String email);

    List<SiteMemberAuth> getByPw(String pw);

    List<SiteMemberAuth> getByProvider(AuthProvider provider);

    List<SiteMemberAuth> getByProviderId(String providerId);

    List<SiteMemberAuth> getByProviderAndProviderId(AuthProvider provider, String providerId);

    List<SiteMemberAuth> getByFailedAttempt(Integer failedAttempt);

    Optional<SiteMemberAuth> getByOriginalMember(SiteMember originalMember);
}
