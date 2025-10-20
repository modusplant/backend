package kr.modusplant.domains.term.framework.out.jpa.mapper;

import kr.modusplant.domains.term.domain.aggregate.Term;
import kr.modusplant.domains.term.domain.vo.TermContent;
import kr.modusplant.domains.term.domain.vo.TermId;
import kr.modusplant.domains.term.domain.vo.TermName;
import kr.modusplant.domains.term.domain.vo.TermVersion;
import kr.modusplant.domains.term.framework.out.jpa.mapper.supers.TermJpaMapper;
import kr.modusplant.framework.out.jpa.entity.TermEntity;
import org.springframework.stereotype.Component;

@Component
public class TermJpaMapperImpl implements TermJpaMapper {

    @Override
    public TermEntity toTermNewEntity(Term term) {
        return TermEntity.builder()
                .name(term.getTermName().getValue())
                .content(term.getTermContent().getValue())
                .version(term.getTermVersion().getValue())
                .build();
    }

    @Override
    public Term toTerm(TermEntity entity) {
        return Term.create(
                TermId.fromUuid(entity.getUuid()),
                TermName.create(entity.getName()),
                TermContent.create(entity.getContent()),
                TermVersion.create(entity.getVersion())
        );
    }
}
