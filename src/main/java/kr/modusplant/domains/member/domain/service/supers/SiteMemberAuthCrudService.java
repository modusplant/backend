<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/domain/service/supers/SiteMemberAuthCrudService.java
package kr.modusplant.domains.member.domain.service.supers;

import kr.modusplant.domains.commons.domain.supers.UuidCrudService;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.enums.AuthProvider;
========
package kr.modusplant.api.crud.member.domain.service;

import kr.modusplant.api.crud.common.domain.supers.UuidCrudService;
import kr.modusplant.api.crud.member.domain.model.SiteMember;
import kr.modusplant.api.crud.member.domain.model.SiteMemberAuth;
import kr.modusplant.api.crud.member.enums.AuthProvider;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/main/java/kr/modusplant/api/crud/member/domain/service/SiteMemberAuthService.java

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
