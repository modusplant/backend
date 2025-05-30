package kr.modusplant.domains.term.domain.service;

import jakarta.persistence.EntityExistsException;
import kr.modusplant.domains.term.persistence.entity.TermEntity;
import kr.modusplant.domains.term.persistence.repository.TermRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.NAME;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TermValidationService {

    private final TermRepository termRepository;

    public void validateExistedUuid(UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (termRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, TermEntity.class);
        }
    }

    public void validateExistedName(String name) {
        if (termRepository.findByName(name).isPresent()) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY.getValue(), NAME, name, TermEntity.class));
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || termRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, TermEntity.class);
        }
    }
}