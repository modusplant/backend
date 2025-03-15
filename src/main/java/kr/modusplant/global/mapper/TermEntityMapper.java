package kr.modusplant.global.mapper;

import kr.modusplant.global.domain.model.Term;
import kr.modusplant.global.persistence.entity.TermEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

@Mapper
public interface TermEntityMapper {
    @BeanMapping(ignoreByDefault = true)
    default TermEntity createTermEntity(Term term) {
        return TermEntity.builder()
                .name(term.getName())
                .content(term.getContent())
                .version(term.getVersion()).build();
    }

    @BeanMapping(ignoreByDefault = true)
    default TermEntity updateTermEntity(Term term) {
        return TermEntity.builder()
                .uuid(term.getUuid())
                .name(term.getName())
                .content(term.getContent())
                .version(term.getVersion()).build();
    }

    Term toTerm(TermEntity TermEntity);
}
