package kr.modusplant.domains.member.domain.service.supers;

import kr.modusplant.domains.commons.app.service.supers.UuidCrudService;
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
