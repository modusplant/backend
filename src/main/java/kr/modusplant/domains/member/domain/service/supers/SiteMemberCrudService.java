<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/domain/service/supers/SiteMemberCrudService.java
package kr.modusplant.domains.member.domain.service.supers;

import kr.modusplant.domains.commons.domain.supers.UuidCrudService;
import kr.modusplant.domains.member.domain.model.SiteMember;
========
package kr.modusplant.api.crud.member.domain.service;

import kr.modusplant.api.crud.common.domain.supers.UuidCrudService;
import kr.modusplant.api.crud.member.domain.model.SiteMember;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/main/java/kr/modusplant/api/crud/member/domain/service/SiteMemberService.java

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SiteMemberCrudService extends UuidCrudService<SiteMember> {
    List<SiteMember> getByNickname(String nickname);

    List<SiteMember> getByBirthDate(LocalDate birthDate);

    List<SiteMember> getByIsActive(Boolean isActive);

    List<SiteMember> getByIsDisabledByLinking(Boolean isDisabledByLinking);

    List<SiteMember> getByIsBanned(Boolean isBanned);

    List<SiteMember> getByIsDeleted(Boolean isDeleted);

    List<SiteMember> getByLoggedInAt(LocalDateTime loggedInAt);}
