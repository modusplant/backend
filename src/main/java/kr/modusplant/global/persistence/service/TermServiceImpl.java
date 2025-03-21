package kr.modusplant.global.persistence.service;

import kr.modusplant.global.domain.model.Term;
import kr.modusplant.global.domain.service.crud.TermService;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.global.mapper.TermEntityMapper;
import kr.modusplant.global.mapper.TermEntityMapperImpl;
import kr.modusplant.global.persistence.entity.TermEntity;
import kr.modusplant.global.persistence.repository.TermJpaRepository;
import lombok.RequiredArgsConstructor;
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
public class TermServiceImpl implements TermService {
    private final TermJpaRepository termRepository;
    private final TermEntityMapper termEntityMapper = new TermEntityMapperImpl();

    @Override
    public List<Term> getAll() {
        return termRepository.findAll().stream().map(termEntityMapper::toTerm).toList();
    }

    @Override
    public List<Term> getByVersion(String version) {
        return termRepository.findByVersion(version).stream().map(termEntityMapper::toTerm).toList();
    }

    @Override
    public Optional<Term> getByUuid(UUID uuid) {
        Optional<TermEntity> termOrEmpty = termRepository.findByUuid(uuid);
        return termOrEmpty.isEmpty() ? Optional.empty() : Optional.of(termEntityMapper.toTerm(termOrEmpty.orElseThrow()));
    }

    @Override
    public Optional<Term> getByName(String name) {
        Optional<TermEntity> termOrEmpty = termRepository.findByName(name);
        return termOrEmpty.isEmpty() ? Optional.empty() : Optional.of(termEntityMapper.toTerm(termOrEmpty.orElseThrow()));
    }

    @Override
    @Transactional
    public Term insert(Term term) {
        validateExistedEntity(term.getUuid());
        return termEntityMapper.toTerm(termRepository.save(termEntityMapper.createTermEntity(term)));
    }

    @Override
    @Transactional
    public Term update(Term term) {
        validateNotFoundEntity(term.getUuid());
        return termEntityMapper.toTerm(termRepository.save(termEntityMapper.updateTermEntity(term)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        validateNotFoundEntity(uuid);
        termRepository.deleteByUuid(uuid);
    }

    private void validateExistedEntity(UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (termRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, TermEntity.class);
        }
    }

    private void validateNotFoundEntity(UUID uuid) {
        if (uuid == null || termRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, TermEntity.class);
        }
    }
}
