package kr.modusplant.domains.communication.qna.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;
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
        if (qnaCategoryRepository.findByOrder(order).isPresent()) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY.getValue(), ORDER, order, QnaCategoryEntity.class));
        }
    }

    public void validateExistedCategory(String category) {
        if (qnaCategoryRepository.findByCategory(category).isPresent()) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY.getValue(), CATEGORY, category, QnaCategoryEntity.class));
        }
    }

    public void validateNotFoundOrder(Integer order) {
        if (order == null || qnaCategoryRepository.findByOrder(order).isEmpty()) {
            throw new EntityNotFoundException(getFormattedExceptionMessage(NOT_FOUND_ENTITY.getValue(), ORDER, order, QnaCategoryEntity.class));
        }
    }
}