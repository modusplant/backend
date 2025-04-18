package kr.modusplant.domains.term.domain.service;

import kr.modusplant.domains.common.context.DomainServiceOnlyContext;
import kr.modusplant.domains.term.app.http.response.TermResponse;
import kr.modusplant.domains.term.app.service.TermApplicationService;
import kr.modusplant.domains.term.common.app.http.request.TermRequestTestUtils;
import kr.modusplant.domains.term.common.app.http.response.TermResponseTestUtils;
import kr.modusplant.domains.term.common.util.entity.TermEntityTestUtils;
import kr.modusplant.domains.term.mapper.TermAppInfraMapper;
import kr.modusplant.domains.term.mapper.TermAppInfraMapperImpl;
import kr.modusplant.domains.term.persistence.entity.TermEntity;
import kr.modusplant.domains.term.persistence.repository.TermRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainServiceOnlyContext
class TermApplicationServiceTest implements TermRequestTestUtils, TermResponseTestUtils, TermEntityTestUtils {

    private final TermApplicationService termApplicationService;
    private final TermRepository termRepository;
    private final TermAppInfraMapper termAppInfraMapper = new TermAppInfraMapperImpl();

    @Autowired
    TermApplicationServiceTest(TermApplicationService termApplicationService, TermRepository termRepository) {
        this.termApplicationService = termApplicationService;
        this.termRepository = termRepository;
    }

    @DisplayName("uuid로 약관 얻기")
    @Test
    void getByUuidTest() {
        // given
        TermEntity termEntity = termAppInfraMapper.toTermEntity(termsOfUseInsertRequest);
        TermEntity returnedTermEntity = createTermsOfUseEntityWithUuid();

        given(termRepository.save(termEntity)).willReturn(returnedTermEntity);
        given(termRepository.findByUuid(termsOfUseResponseWithUuid.uuid())).willReturn(Optional.of(returnedTermEntity));

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

        given(termRepository.save(termEntity)).willReturn(returnedTermEntity);
        given(termRepository.findByName(termsOfUseResponseWithUuid.name())).willReturn(Optional.empty()).willReturn(Optional.of(returnedTermEntity));

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
        given(termRepository.findByName(termEntity.getName())).willReturn(Optional.empty());
        given(termRepository.findByVersion(termsOfUseResponseWithUuid.version())).willReturn(List.of(returnedTermEntity));

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

        given(termRepository.save(termEntity)).willReturn(termEntity).willReturn(updatedTermEntity);
        given(termRepository.findByUuid(uuid)).willReturn(Optional.of(termEntity));
        given(termRepository.findByName(updatedTermEntity.getName())).willReturn(Optional.empty()).willReturn(Optional.of(updatedTermEntity));

        // when
        termApplicationService.insert(termsOfUseInsertRequest);
        termApplicationService.update(termsOfUseUpdateRequest, uuid);

        // then
        assertThat(termApplicationService.getByName(updatedTermEntity.getName()).orElseThrow().content()).isEqualTo(updatedContent);
    }

    @DisplayName("uuid로 약관 제거")
    @Test
    void removeByUuidTest() {
        // given
        UUID uuid = termsOfUseWithUuid.getUuid();
        TermEntity termEntity = termAppInfraMapper.toTermEntity(termsOfUseInsertRequest);

        given(termRepository.save(termEntity)).willReturn(termEntity);
        given(termRepository.findByUuid(uuid)).willReturn(Optional.of(termEntity)).willReturn(Optional.empty());
        given(termRepository.findByName(termEntity.getName())).willReturn(Optional.empty());
        willDoNothing().given(termRepository).deleteByUuid(uuid);

        // when
        termApplicationService.insert(termsOfUseInsertRequest);
        termApplicationService.removeByUuid(uuid);

        // then
        assertThat(termApplicationService.getByUuid(uuid)).isEmpty();
    }
}