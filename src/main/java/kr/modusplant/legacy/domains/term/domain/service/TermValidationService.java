package kr.modusplant.legacy.domains.term.domain.service;

import kr.modusplant.framework.out.jpa.repository.TermRepository;
import kr.modusplant.legacy.domains.term.error.TermExistsException;
import kr.modusplant.legacy.domains.term.error.TermNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
        if (termRepository.existsByUuid(uuid)) {
            throw new TermExistsException();
        }
    }

    public void validateExistedName(String name) {
        if (termRepository.existsByName(name)) {
            throw new TermExistsException();
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !termRepository.existsByUuid(uuid)) {
            throw new TermNotFoundException();
        }
    }
}