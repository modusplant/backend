package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.communication.qna.app.http.request.QnaCategoryInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaCategoryResponse;
import kr.modusplant.domains.communication.qna.domain.service.QnaCategoryValidationService;
import kr.modusplant.domains.communication.qna.mapper.QnaCategoryAppInfraMapper;
import kr.modusplant.domains.communication.qna.mapper.QnaCategoryAppInfraMapperImpl;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QnaCategoryApplicationService {

    private final QnaCategoryValidationService validationService;
    private final QnaCategoryRepository qnaCategoryRepository;
    private final QnaCategoryAppInfraMapper qnaCategoryAppInfraMapper = new QnaCategoryAppInfraMapperImpl();

    @Cacheable(value = "qna_categories")
    public List<QnaCategoryResponse> getAll() {
        return qnaCategoryRepository.findAll().stream().map(qnaCategoryAppInfraMapper::toQnaCategoryResponse).toList();
    }

    public Optional<QnaCategoryResponse> getByUuid(UUID uuid) {
        Optional<QnaCategoryEntity> qnaCategoryOrEmpty = qnaCategoryRepository.findByUuid(uuid);
        return qnaCategoryOrEmpty.isEmpty() ? Optional.empty() : Optional.of(qnaCategoryAppInfraMapper.toQnaCategoryResponse(qnaCategoryOrEmpty.orElseThrow()));
    }

    public Optional<QnaCategoryResponse> getByOrder(Integer order) {
        Optional<QnaCategoryEntity> qnaCategoryOrEmpty = qnaCategoryRepository.findByOrder(order);
        return qnaCategoryOrEmpty.isEmpty() ? Optional.empty() : Optional.of(qnaCategoryAppInfraMapper.toQnaCategoryResponse(qnaCategoryOrEmpty.orElseThrow()));
    }

    public Optional<QnaCategoryResponse> getByCategory(String category) {
        Optional<QnaCategoryEntity> qnaCategoryOrEmpty = qnaCategoryRepository.findByCategory(category);
        return qnaCategoryOrEmpty.isEmpty() ? Optional.empty() : Optional.of(qnaCategoryAppInfraMapper.toQnaCategoryResponse(qnaCategoryOrEmpty.orElseThrow()));
    }

    @Transactional
    @CacheEvict(value = "qna_categories", allEntries = true)
    public QnaCategoryResponse insert(QnaCategoryInsertRequest qnaCategoryInsertRequest) {
        validationService.validateExistedCategory(qnaCategoryInsertRequest.category());
        validationService.validateExistedOrder(qnaCategoryInsertRequest.order());
        return qnaCategoryAppInfraMapper.toQnaCategoryResponse(qnaCategoryRepository.save(qnaCategoryAppInfraMapper.toQnaCategoryEntity(qnaCategoryInsertRequest)));
    }

    @Transactional
    @CacheEvict(value = "qna_categories", allEntries = true)
    public void removeByUuid(UUID uuid) {
        validationService.validateNotFoundUuid(uuid);
        qnaCategoryRepository.deleteByUuid(uuid);
    }
}
