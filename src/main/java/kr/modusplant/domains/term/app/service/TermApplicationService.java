package kr.modusplant.domains.term.app.service;

import kr.modusplant.domains.term.app.http.request.TermInsertRequest;
import kr.modusplant.domains.term.app.http.request.TermUpdateRequest;
import kr.modusplant.domains.term.app.http.response.TermResponse;
import kr.modusplant.domains.term.domain.service.TermValidationService;
import kr.modusplant.domains.term.mapper.TermAppInfraMapper;
import kr.modusplant.domains.term.mapper.TermAppInfraMapperImpl;
import kr.modusplant.domains.term.persistence.entity.TermEntity;
import kr.modusplant.domains.term.persistence.repository.TermRepository;
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
public class TermApplicationService {

    private final TermValidationService validationService;
    private final TermRepository termRepository;
    private final TermAppInfraMapper termAppInfraMapper = new TermAppInfraMapperImpl();

    @Cacheable(value = "terms")
    public List<TermResponse> getAll() {
        return termRepository.findAll().stream().map(termAppInfraMapper::toTermResponse).toList();
    }

    @Cacheable(value = "terms", key = "'version:' + #version", unless = "#result == null or #result.isEmpty()")
    public List<TermResponse> getByVersion(String version) {
        return termRepository.findByVersion(version).stream().map(termAppInfraMapper::toTermResponse).toList();
    }

    public Optional<TermResponse> getByUuid(UUID uuid) {
        Optional<TermEntity> termOrEmpty = termRepository.findByUuid(uuid);
        return termOrEmpty.isEmpty() ? Optional.empty() : Optional.of(termAppInfraMapper.toTermResponse(termOrEmpty.orElseThrow()));
    }

    public Optional<TermResponse> getByName(String name) {
        Optional<TermEntity> termOrEmpty = termRepository.findByName(name);
        return termOrEmpty.isEmpty() ? Optional.empty() : Optional.of(termAppInfraMapper.toTermResponse(termOrEmpty.orElseThrow()));
    }

    @Transactional
    @CacheEvict(value = "terms", allEntries = true)
    public TermResponse insert(TermInsertRequest termInsertRequest) {
        validationService.validateExistedName(termInsertRequest.name());
        return termAppInfraMapper.toTermResponse(termRepository.save(termAppInfraMapper.toTermEntity(termInsertRequest)));
    }

    @Transactional
    @CacheEvict(value = "terms", allEntries = true)
    public TermResponse update(TermUpdateRequest termUpdateRequest) {
        UUID uuid = termUpdateRequest.uuid();
        validationService.validateNotFoundUuid(uuid);
        TermEntity termEntity = termRepository.findByUuid(uuid).orElseThrow();
        termEntity.updateContent(termUpdateRequest.content());
        termEntity.updateVersion(termUpdateRequest.version());
        return termAppInfraMapper.toTermResponse(termRepository.save(termEntity));
    }

    @Transactional
    @CacheEvict(value = "terms", allEntries = true)
    public void removeByUuid(UUID uuid) {
        validationService.validateNotFoundUuid(uuid);
        termRepository.deleteByUuid(uuid);
    }
}
