package kr.modusplant.legacy.domains.term.app.service;

import kr.modusplant.framework.out.jpa.entity.TermEntity;
import kr.modusplant.framework.out.jpa.repository.TermRepository;
import kr.modusplant.legacy.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.legacy.domains.term.app.http.response.TermResponse;
import kr.modusplant.legacy.domains.term.common.util.app.http.request.TermRequestTestUtils;
import kr.modusplant.legacy.domains.term.common.util.app.http.response.TermResponseTestUtils;
import kr.modusplant.legacy.domains.term.common.util.entity.TermEntityTestUtils;
import kr.modusplant.legacy.domains.term.mapper.TermAppInfraMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainsServiceWithoutValidationServiceContext
class TermApplicationServiceTest implements TermRequestTestUtils, TermResponseTestUtils, TermEntityTestUtils {

    private final TermApplicationService termApplicationService;
    private final TermRepository termRepository;
    private final TermAppInfraMapper termAppInfraMapper;

    @Autowired
    TermApplicationServiceTest(TermApplicationService termApplicationService, TermRepository termRepository, TermAppInfraMapper termAppInfraMapper) {
        this.termApplicationService = termApplicationService;
        this.termRepository = termRepository;
        this.termAppInfraMapper = termAppInfraMapper;
    }

    @DisplayName("uuid로 약관 얻기")
    @Test
    void getByUuidTest() {
        // given
        TermEntity termEntity = termAppInfraMapper.toTermEntity(termsOfUseInsertRequest);
        TermEntity returnedTermEntity = createTermsOfUseEntityWithUuid();

        given(termRepository.save(termEntity)).willReturn(returnedTermEntity);
        given(termRepository.findByUuid(termsOfUseResponse.uuid())).willReturn(Optional.of(returnedTermEntity));

        // when
        TermResponse termResponse = termApplicationService.insert(termsOfUseInsertRequest);

        // then
        assertThat(termApplicationService.getByUuid(termResponse.uuid()).orElseThrow()).isEqualTo(termResponse);
    }

    @DisplayName("name으로 약관 얻기")
    @Test
    void getByNameTest() {
        // given
        TermEntity termEntity = termAppInfraMapper.toTermEntity(termsOfUseInsertRequest);
        TermEntity returnedTermEntity = createTermsOfUseEntityWithUuid();
        String name = termsOfUseResponse.name();

        given(termRepository.save(termEntity)).willReturn(returnedTermEntity);
        given(termRepository.existsByName(name)).willReturn(false);
        given(termRepository.findByName(name)).willReturn(Optional.of(returnedTermEntity));

        // when
        TermResponse termResponse = termApplicationService.insert(termsOfUseInsertRequest);

        // then
        assertThat(termApplicationService.getByName(termResponse.name()).orElseThrow()).isEqualTo(termResponse);
    }

    @DisplayName("version으로 약관 얻기")
    @Test
    void getByVersionTest() {
        // given
        TermEntity termEntity = termAppInfraMapper.toTermEntity(termsOfUseInsertRequest);
        TermEntity returnedTermEntity = createTermsOfUseEntityWithUuid();

        given(termRepository.save(termEntity)).willReturn(returnedTermEntity);
        given(termRepository.existsByName(termEntity.getName())).willReturn(false);
        given(termRepository.findByVersion(termsOfUseResponse.version())).willReturn(List.of(returnedTermEntity));

        // when
        TermResponse termResponse = termApplicationService.insert(termsOfUseInsertRequest);

        // then
        assertThat(termApplicationService.getByVersion(termResponse.version()).getFirst()).isEqualTo(termResponse);
    }

    @DisplayName("빈 약관 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        TermEntity termEntity = createTermsOfUseEntity();
        UUID uuid = termEntity.getUuid();
        String name = termEntity.getName();

        // getByUuid
        // given & when
        given(termRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(termApplicationService.getByUuid(uuid)).isEmpty();

        // getByName
        // given & when
        given(termRepository.findByName(name)).willReturn(Optional.empty());

        // then
        assertThat(termApplicationService.getByName(name)).isEmpty();
    }

    @DisplayName("약관 갱신")
    @Test
    void updateTest() {
        // given
        UUID uuid = termsOfUseWithUuid.getUuid();
        String updatedContent = "갱신된 컨텐츠";
        TermEntity termEntity = termAppInfraMapper.toTermEntity(termsOfUseInsertRequest);
        TermEntity updatedTermEntity = TermEntity.builder().uuid(uuid).termEntity(termEntity).content(updatedContent).build();
        String name = updatedTermEntity.getName();

        given(termRepository.save(termEntity)).willReturn(termEntity).willReturn(updatedTermEntity);
        given(termRepository.existsByUuid(uuid)).willReturn(true);
        given(termRepository.existsByName(name)).willReturn(false).willReturn(true);
        given(termRepository.findByName(name)).willReturn(Optional.of(updatedTermEntity));

        // when
        termApplicationService.insert(termsOfUseInsertRequest);
        termApplicationService.update(termsOfUseUpdateRequest);

        // then
        assertThat(termApplicationService.getByName(name).orElseThrow().content()).isEqualTo(updatedContent);
    }

    @DisplayName("uuid로 약관 제거")
    @Test
    void removeByUuidTest() {
        // given
        UUID uuid = termsOfUseWithUuid.getUuid();
        TermEntity termEntity = termAppInfraMapper.toTermEntity(termsOfUseInsertRequest);

        given(termRepository.save(termEntity)).willReturn(termEntity);
        given(termRepository.existsByUuid(uuid)).willReturn(true).willReturn(false);
        given(termRepository.findByName(termEntity.getName())).willReturn(Optional.empty());
        given(termRepository.findByUuid(uuid)).willReturn(Optional.empty());
        willDoNothing().given(termRepository).deleteByUuid(uuid);

        // when
        termApplicationService.insert(termsOfUseInsertRequest);
        termApplicationService.removeByUuid(uuid);

        // then
        assertThat(termApplicationService.getByUuid(uuid)).isEmpty();
    }
}