package kr.modusplant.domains.communication.domain.service;

import kr.modusplant.domains.communication.error.CommunicationExistsException;
import kr.modusplant.domains.communication.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.persistence.repository.CommSecondaryCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommCategoryValidationService {

    private final CommSecondaryCategoryRepository commCategoryRepository;

    public void validateExistedOrder(Integer order) {
        if (order == null) {
            return;
        }
        if (commCategoryRepository.existsByOrder(order)) {
            throw CommunicationExistsException.ofCategory();
        }
    }

    public void validateExistedCategory(String category) {
        if (commCategoryRepository.existsByCategory(category)) {
            throw CommunicationExistsException.ofCategory();
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !commCategoryRepository.existsByUuid(uuid)) {
            throw CommunicationNotFoundException.ofCategory();
        }
    }
}