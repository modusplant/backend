package kr.modusplant.legacy.domains.communication.app.service;

import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.repository.CommSecondaryCategoryJpaRepository;
import kr.modusplant.legacy.domains.communication.app.http.request.CommCategoryInsertRequest;
import kr.modusplant.legacy.domains.communication.app.http.response.CommCategoryResponse;
import kr.modusplant.legacy.domains.communication.domain.service.CommCategoryValidationService;
import kr.modusplant.legacy.domains.communication.mapper.CommSecondaryCategoryAppInfraMapper;
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
public class CommSecondaryCategoryApplicationService {

    private final CommCategoryValidationService validationService;
    private final CommSecondaryCategoryJpaRepository commCategoryRepository;
    private final CommSecondaryCategoryAppInfraMapper commCategoryAppInfraMapper;

    @Cacheable(value = "comm_categories")
    public List<CommCategoryResponse> getAll() {
        return commCategoryRepository.findAll().stream().map(commCategoryAppInfraMapper::toCommCategoryResponse).toList();
    }

    public Optional<CommCategoryResponse> getByUuid(UUID uuid) {
        Optional<CommSecondaryCategoryEntity> commCategoryOrEmpty = commCategoryRepository.findByUuid(uuid);
        return commCategoryOrEmpty.isEmpty() ? Optional.empty() : Optional.of(commCategoryAppInfraMapper.toCommCategoryResponse(commCategoryOrEmpty.orElseThrow()));
    }

    public Optional<CommCategoryResponse> getByOrder(Integer order) {
        Optional<CommSecondaryCategoryEntity> commCategoryOrEmpty = commCategoryRepository.findByOrder(order);
        return commCategoryOrEmpty.isEmpty() ? Optional.empty() : Optional.of(commCategoryAppInfraMapper.toCommCategoryResponse(commCategoryOrEmpty.orElseThrow()));
    }

    public Optional<CommCategoryResponse> getByCategory(String category) {
        Optional<CommSecondaryCategoryEntity> commCategoryOrEmpty = commCategoryRepository.findByCategory(category);
        return commCategoryOrEmpty.isEmpty() ? Optional.empty() : Optional.of(commCategoryAppInfraMapper.toCommCategoryResponse(commCategoryOrEmpty.orElseThrow()));
    }

    @Transactional
    @CacheEvict(value = "comm_categories", allEntries = true)
    public CommCategoryResponse insert(CommCategoryInsertRequest commCategoryInsertRequest) {
        validationService.validateExistedCategory(commCategoryInsertRequest.category());
        validationService.validateExistedOrder(commCategoryInsertRequest.order());
        return commCategoryAppInfraMapper.toCommCategoryResponse(commCategoryRepository.save(commCategoryAppInfraMapper.toCommCategoryEntity(commCategoryInsertRequest)));
    }

    @Transactional
    @CacheEvict(value = "comm_categories", allEntries = true)
    public void removeByUuid(UUID uuid) {
        validationService.validateNotFoundUuid(uuid);
        commCategoryRepository.deleteByUuid(uuid);
    }
}
