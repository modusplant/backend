package kr.modusplant.global.persistence.service;

import kr.modusplant.global.domain.model.Term;
import kr.modusplant.global.domain.service.crud.TermService;
import kr.modusplant.global.mapper.TermEntityMapper;
import kr.modusplant.global.mapper.TermEntityMapperImpl;
import kr.modusplant.global.persistence.entity.TermEntity;
import kr.modusplant.global.persistence.repository.TermJpaRepository;
import kr.modusplant.support.context.ServiceOnlyContext;
import kr.modusplant.support.util.domain.TermTestUtils;
import kr.modusplant.support.util.entity.TermEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ServiceOnlyContext
class TermServiceImplTest implements TermTestUtils, TermEntityTestUtils {

    private final TermService termService;
    private final TermJpaRepository termRepository;
    private final TermEntityMapper termMapper = new TermEntityMapperImpl();

    @Autowired
    TermServiceImplTest(TermService termService, TermJpaRepository termRepository) {
        this.termService = termService;
        this.termRepository = termRepository;
    }

    @DisplayName("uuid로 회원 얻기")
    @Test
    void getByUuidTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntity();
        Term term = termMapper.toTerm(termEntity);

        given(termRepository.findByUuid(termEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(termEntity));
        given(termRepository.save(termEntity)).willReturn(termEntity);

        // when
        term = termService.insert(term);

        // then
        assertThat(termService.getByUuid(term.getUuid()).orElseThrow()).isEqualTo(term);
    }

    @DisplayName("name으로 회원 얻기")
    @Test
    void getByNameTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntity();
        Term term = termMapper.toTerm(termEntity);

        given(termRepository.findByUuid(termEntity.getUuid())).willReturn(Optional.empty());
        given(termRepository.save(termEntity)).willReturn(termEntity);
        given(termRepository.findByName(termEntity.getName())).willReturn(Optional.of(termEntity));

        // when
        term = termService.insert(term);

        // then
        assertThat(termService.getByName(termEntity.getName()).orElseThrow()).isEqualTo(term);
    }

    @DisplayName("version으로 회원 얻기")
    @Test
    void getByVersionTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntity();
        Term term = termMapper.toTerm(termEntity);

        given(termRepository.findByUuid(termEntity.getUuid())).willReturn(Optional.empty());
        given(termRepository.save(termEntity)).willReturn(termEntity);
        given(termRepository.findByVersion(termEntity.getVersion())).willReturn(List.of(termEntity));

        // when
        term = termService.insert(term);

        // then
        assertThat(termService.getByVersion(termEntity.getVersion()).getFirst()).isEqualTo(term);
    }

    @DisplayName("uuid로 회원 제거")
    @Test
    void removeByUuidTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntity();
        Term term = termMapper.toTerm(termEntity);

        given(termRepository.findByUuid(term.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(termEntity));
        given(termRepository.save(termEntity)).willReturn(termEntity);
        given(termRepository.findAll()).willReturn(emptyList());
        willDoNothing().given(termRepository).deleteByUuid(term.getUuid());

        // when
        term = termService.insert(term);
        termService.removeByUuid(term.getUuid());

        // then
        assertThat(termService.getAll()).isEmpty();
    }
}