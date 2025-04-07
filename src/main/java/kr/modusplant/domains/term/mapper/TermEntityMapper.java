<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/term/mapper/TermEntityMapper.java
package kr.modusplant.domains.term.mapper;

import kr.modusplant.domains.term.domain.model.Term;
import kr.modusplant.domains.term.persistence.entity.TermEntity;
========
package kr.modusplant.api.crud.term.mapper;

import kr.modusplant.api.crud.term.domain.model.Term;
import kr.modusplant.api.crud.term.persistence.entity.TermEntity;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/main/java/kr/modusplant/api/crud/term/mapper/TermEntityMapper.java
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static kr.modusplant.global.vo.CamelCaseWord.TERM;

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

    @Mapping(target = TERM, ignore = true)
    Term toTerm(TermEntity TermEntity);
}
