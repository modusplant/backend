<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/domain/service/supers/SiteMemberCrudService.java
package kr.modusplant.domains.member.domain.service.supers;
========
package kr.modusplant.api.crud.member.domain.service.supers;
>>>>>>>> 4cc54db (MP-145 :truck: Rename: 서비스 및 서비스 구현체 파일 이동):src/main/java/kr/modusplant/domains/member/domain/service/supers/supers/SiteMemberService.java

import kr.modusplant.domains.commons.domain.supers.UuidCrudService;
import kr.modusplant.domains.member.domain.model.SiteMember;

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
