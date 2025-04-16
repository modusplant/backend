<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/domain/service/supers/SiteMemberAuthCrudService.java
package kr.modusplant.domains.member.domain.service.supers;
========
package kr.modusplant.api.crud.member.domain.service.supers;
>>>>>>>> 4cc54db (MP-145 :truck: Rename: 서비스 및 서비스 구현체 파일 이동):src/main/java/kr/modusplant/domains/member/domain/service/supers/supers/SiteMemberAuthService.java

import kr.modusplant.domains.commons.domain.supers.UuidCrudService;
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
