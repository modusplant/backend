package kr.modusplant.domains.term.domain.service;

import jakarta.persistence.EntityExistsException;
import kr.modusplant.domains.term.domain.model.Term;
import kr.modusplant.domains.term.domain.service.supers.TermCrudService;
import kr.modusplant.domains.term.mapper.TermEntityMapper;
import kr.modusplant.domains.term.mapper.TermEntityMapperImpl;
import kr.modusplant.domains.term.persistence.entity.TermEntity;
import kr.modusplant.domains.term.persistence.repository.TermCrudJpaRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.NAME;
import static kr.modusplant.global.vo.ExceptionMessage.EXISTED_ENTITY;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TermCrudServiceImpl implements TermCrudService {
    private final TermCrudJpaRepository termRepository;
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
        validateExistedTermUuid(term.getUuid());
        validateExistedName(term.getName());
        return termEntityMapper.toTerm(termRepository.save(termEntityMapper.createTermEntity(term)));
    }

    @Override
    @Transactional
    public Term update(Term term) {
        validateNotFoundTermUuid(term.getUuid());
        return termEntityMapper.toTerm(termRepository.save(termEntityMapper.updateTermEntity(term)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        validateNotFoundTermUuid(uuid);
        termRepository.deleteByUuid(uuid);
    }

    private void validateExistedTermUuid(UUID uuid) {
        if (uuid == null) {
            return;
        }
        if (termRepository.findByUuid(uuid).isPresent()) {
            throw new EntityExistsWithUuidException(uuid, TermEntity.class);
        }
    }

    private void validateExistedName(String name) {
        if (termRepository.findByName(name).isPresent()) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY, NAME, name, TermEntity.class));
        }
    }

    private void validateNotFoundTermUuid(UUID uuid) {
        if (uuid == null || termRepository.findByUuid(uuid).isEmpty()) {
            throw new EntityNotFoundWithUuidException(uuid, TermEntity.class);
        }
    }
}
