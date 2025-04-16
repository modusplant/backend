package kr.modusplant.domains.term.domain.service;

import kr.modusplant.domains.term.domain.model.Term;
import kr.modusplant.domains.term.domain.service.supers.TermCrudService;
import kr.modusplant.domains.term.mapper.TermEntityMapper;
import kr.modusplant.domains.term.mapper.TermEntityMapperImpl;
import kr.modusplant.domains.term.persistence.entity.TermEntity;
import kr.modusplant.domains.term.persistence.repository.TermCrudJpaRepository;
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
        return termEntityMapper.toTerm(termRepository.save(termEntityMapper.createTermEntity(term)));
    }

    @Override
    @Transactional
    public Term update(Term term) {
        return termEntityMapper.toTerm(termRepository.save(termEntityMapper.updateTermEntity(term)));
    }

    @Override
    @Transactional
    public void removeByUuid(UUID uuid) {
        termRepository.deleteByUuid(uuid);
    }
}
