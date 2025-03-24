package kr.modusplant.global.domain.service.crud;

import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.service.crud.supers.UuidCrudService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SiteMemberService extends UuidCrudService<SiteMember> {
    List<SiteMember> getByNickname(String nickname);

    List<SiteMember> getByBirthDate(LocalDate birthDate);

    List<SiteMember> getByIsActive(Boolean isActive);

    List<SiteMember> getByIsDisabledByLinking(Boolean isDisabledByLinking);

    List<SiteMember> getByIsBanned(Boolean isBanned);

    List<SiteMember> getByIsDeleted(Boolean isDeleted);

    List<SiteMember> getByLoggedInAt(LocalDateTime loggedInAt);}
