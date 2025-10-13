package kr.modusplant.domains.term.framework.out.jpa.repository;

import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.domain.vo.TermId;
import kr.modusplant.domains.term.framework.out.jpa.mapper.TermJpaMapperImpl;
import kr.modusplant.domains.term.usecase.port.repository.TermRepository;
import kr.modusplant.framework.out.jpa.repository.TermJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TermRepositoryJpaAdapter implements TermRepository {
    private final TermJpaMapperImpl termJpaMapper;
    private final TermJpaRepository termJpaRepository;

    @Override
    public Term save(Term term) {
        return termJpaMapper.toTerm(termJpaRepository.save(termJpaMapper.toTermEntity(term)));
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
