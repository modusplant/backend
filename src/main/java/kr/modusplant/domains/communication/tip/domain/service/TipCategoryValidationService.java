package kr.modusplant.domains.communication.tip.domain.service;

import kr.modusplant.domains.communication.common.error.CommunicationExistsException;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TipCategoryValidationService {

    private final TipCategoryRepository tipCategoryRepository;

    public void validateExistedOrder(Integer order) {
        if (tipCategoryRepository.existsByOrder(order)) {
            throw CommunicationExistsException.ofCategory();
        }
    }

    public void validateExistedCategory(String category) {
        if (tipCategoryRepository.existsByCategory(category)) {
            throw CommunicationExistsException.ofCategory();
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (!tipCategoryRepository.existsByUuid(uuid)) {
            throw CommunicationNotFoundException.ofCategory();
        }
    }
}