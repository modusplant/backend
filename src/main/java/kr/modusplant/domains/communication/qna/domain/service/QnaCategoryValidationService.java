package kr.modusplant.domains.communication.qna.domain.service;

import jakarta.persistence.EntityExistsException;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.CATEGORY;
import static kr.modusplant.global.vo.CamelCaseWord.ORDER;

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
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY, ORDER, order, QnaCategoryEntity.class));
        }
    }

    public void validateExistedCategory(String category) {
        if (qnaCategoryRepository.existsByCategory(category)) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY, CATEGORY, category, QnaCategoryEntity.class));
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !qnaCategoryRepository.existsByUuid(uuid)) {
            throw new EntityNotFoundWithUuidException(uuid, QnaCategoryEntity.class);
        }
    }
}