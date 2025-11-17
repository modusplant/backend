package kr.modusplant.domains.term.framework.out.jpa.repository;

import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.domain.exception.TermNotFoundException;
import kr.modusplant.domains.term.domain.vo.TermId;
import kr.modusplant.domains.term.framework.out.jpa.mapper.TermJpaMapperImpl;
import kr.modusplant.domains.term.usecase.port.repository.TermRepository;
import kr.modusplant.framework.jpa.entity.TermEntity;
import kr.modusplant.framework.jpa.repository.TermJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TermRepositoryJpaAdapter implements TermRepository {
    private final TermJpaMapperImpl termJpaMapper;
    private final TermJpaRepository termJpaRepository;

    @Override
    public Term save(Term term) {
        if(term.getTermId() == null || term.getTermId().getValue() == null) {
            return termJpaMapper.toTerm(termJpaRepository.save(termJpaMapper.toTermNewEntity(term)));
        } else {
            TermEntity entity = termJpaRepository.findById(term.getTermId().getValue()).orElseThrow(TermNotFoundException::new);
            entity.updateContent(term.getTermContent().getValue());
            return termJpaMapper.toTerm(entity);
        }
    }

    @Override
    public Optional<Term> findById(TermId termId) {
        return termJpaRepository.findById(termId.getValue()).map(termJpaMapper::toTerm);
    }

    @Override
    public List<Term> findAll() {
        return termJpaRepository.findAll().stream().map(termJpaMapper::toTerm).toList();
    }

    @Override
    public boolean isIdExist(TermId termId) {
        return termJpaRepository.existsById(termId.getValue());
    }

    @Override
    public void deleteById(TermId termId) {
        termJpaRepository.deleteById(termId.getValue());
    }


}
