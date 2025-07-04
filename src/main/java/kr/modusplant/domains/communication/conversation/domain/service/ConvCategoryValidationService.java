package kr.modusplant.domains.communication.conversation.domain.service;

import kr.modusplant.domains.communication.common.error.CommunicationExistsException;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConvCategoryValidationService {

    private final ConvCategoryRepository convCategoryRepository;

    public void validateExistedOrder(Integer order) {
        if (order == null) {
            return;
        }
        if (convCategoryRepository.existsByOrder(order)) {
            throw CommunicationExistsException.ofCategory();
        }
    }

    public void validateExistedCategory(String category) {
        if (convCategoryRepository.existsByCategory(category)) {
            throw CommunicationExistsException.ofCategory();
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !convCategoryRepository.existsByUuid(uuid)) {
            throw CommunicationNotFoundException.ofCategory();
        }
    }
}