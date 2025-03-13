package kr.modusplant.global.mapper;

import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.global.domain.model.Term;
import kr.modusplant.global.persistence.entity.TermEntity;
import kr.modusplant.global.persistence.repository.TermJpaRepository;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.util.MapperUtils.map;
import static kr.modusplant.global.vo.CamelCaseWord.NAME;
import static kr.modusplant.global.vo.ExceptionMessage.NOT_FOUND_ENTITY;

@Mapper
public interface TermEntityMapper {
    @BeanMapping(ignoreByDefault = true)
    default TermEntity createTermEntity(Term term) {
        return map(term, TermEntity.builder().build());
    };

    @BeanMapping(ignoreByDefault = true)
    default TermEntity updateTermEntity(Term term, @Context TermJpaRepository termRepository) {
        return map(term, termRepository.findByName(term.getName())
                .orElseThrow(() -> new EntityNotFoundException(getFormattedExceptionMessage(
                        NOT_FOUND_ENTITY, NAME, term.getName(), TermEntity.class))));
    }

    Term toTerm(TermEntity TermEntity);
}
