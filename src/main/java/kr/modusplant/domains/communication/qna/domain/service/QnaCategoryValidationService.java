package kr.modusplant.domains.communication.qna.domain.service;

import kr.modusplant.domains.communication.common.error.CommunicationExistsException;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QnaCategoryValidationService {

    private final QnaCategoryRepository qnaCategoryRepository;

    public void validateExistedOrder(Integer order) {
        if (order == null) {
            return;
        }
        if (qnaCategoryRepository.existsByOrder(order)) {
            throw CommunicationExistsException.ofCategory();
        }
    }

    public void validateExistedCategory(String category) {
        if (qnaCategoryRepository.existsByCategory(category)) {
            throw CommunicationExistsException.ofCategory();
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !qnaCategoryRepository.existsByUuid(uuid)) {
            throw CommunicationNotFoundException.ofCategory();
        }
    }
}