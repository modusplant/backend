package kr.modusplant.legacy.domains.member.app.service;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.legacy.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberUpdateRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberResponseTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainsServiceWithoutValidationServiceContext
class SiteMemberApplicationServiceTest implements SiteMemberRequestTestUtils, SiteMemberResponseTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberApplicationService memberApplicationService;
    private final SiteMemberJpaRepository memberRepository;

    @Autowired
    SiteMemberApplicationServiceTest(SiteMemberApplicationService memberApplicationService, SiteMemberJpaRepository memberRepository) {
        this.memberApplicationService = memberApplicationService;
        this.memberRepository = memberRepository;
    }

    @DisplayName("uuid로 회원 얻기")
    @Test
    void getByUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));

        // when
        SiteMemberResponse memberResponse = memberApplicationService.insert(memberBasicUserInsertRequest);

        // then
        assertThat(memberApplicationService.getByUuid(memberResponse.uuid()).orElseThrow()).isEqualTo(memberResponse);
    }

    @DisplayName("nickname으로 회원 얻기")
    @Test
    void getByNicknameTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.findByNickname(memberEntity.getNickname())).willReturn(Optional.of(memberEntity));

        // when
        SiteMemberResponse memberResponse = memberApplicationService.insert(memberBasicUserInsertRequest);

        // then
        assertThat(memberApplicationService.getByNickname(memberResponse.nickname()).getFirst()).isEqualTo(memberResponse);
    }

    @DisplayName("birthDate으로 회원 얻기")
    @Test
    void getByBirthDateTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.findByBirthDate(memberEntity.getBirthDate())).willReturn(List.of(memberEntity));

        // when
        SiteMemberResponse memberResponse = memberApplicationService.insert(memberBasicUserInsertRequest);

        // then
        assertThat(memberApplicationService.getByBirthDate(memberResponse.birthDate()).getFirst()).isEqualTo(memberResponse);
    }

    @DisplayName("isActive으로 회원 얻기")
    @Test
    void getByIsActiveTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.findByIsActive(memberEntity.getIsActive())).willReturn(List.of(memberEntity));

        // when
        SiteMemberResponse memberResponse = memberApplicationService.insert(memberBasicUserInsertRequest);

        // then
        assertThat(memberApplicationService.getByIsActive(memberResponse.isActive()).getFirst()).isEqualTo(memberResponse);
    }

    @DisplayName("isDisabledByLinking으로 회원 얻기")
    @Test
    void getByIsDisabledByLinkingTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.findByIsDisabledByLinking(memberEntity.getIsDisabledByLinking())).willReturn(List.of(memberEntity));

        // when
        SiteMemberResponse memberResponse = memberApplicationService.insert(memberBasicUserInsertRequest);

        // then
        assertThat(memberApplicationService.getByIsDisabledByLinking(memberEntity.getIsDisabledByLinking()).getFirst()).isEqualTo(memberResponse);
    }

    @DisplayName("isBanned로 회원 얻기")
    @Test
    void getByIsBannedTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.findByIsBanned(memberEntity.getIsBanned())).willReturn(List.of(memberEntity));

        // when
        SiteMemberResponse memberResponse = memberApplicationService.insert(memberBasicUserInsertRequest);

        // then
        assertThat(memberApplicationService.getByIsBanned(memberEntity.getIsBanned()).getFirst()).isEqualTo(memberResponse);
    }

    @DisplayName("isDeleted로 회원 얻기")
    @Test
    void getByIsDeletedTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.findByIsDeleted(memberEntity.getIsDeleted())).willReturn(List.of(memberEntity));

        // when
        SiteMemberResponse memberResponse = memberApplicationService.insert(memberBasicUserInsertRequest);

        // then
        assertThat(memberApplicationService.getByIsDeleted(memberEntity.getIsDeleted()).getFirst()).isEqualTo(memberResponse);
    }

    @DisplayName("loggedInAt으로 회원 얻기")
    @Test
    void getByLoggedInAtTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.findByLoggedInAt(memberEntity.getLoggedInAt())).willReturn(List.of(memberEntity));

        // when
        SiteMemberResponse memberResponse = memberApplicationService.insert(memberBasicUserInsertRequest);

        // then
        assertThat(memberApplicationService.getByLoggedInAt(memberEntity.getLoggedInAt()).getFirst()).isEqualTo(memberResponse);
    }

    @DisplayName("빈 회원 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        UUID uuid = MEMBER_BASIC_USER_UUID;

        // getByUuid
        // given & when
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(memberApplicationService.getByUuid(uuid)).isEmpty();
    }

    @DisplayName("회원 갱신")
    @Test
    void updateTest() {
        // given
        String updatedNickname = "갱신된 닉네임";
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        SiteMemberEntity beforeUpdatedMemberEntity = createMemberBasicUserEntityWithUuid();
        UUID uuid = beforeUpdatedMemberEntity.getUuid();
        SiteMemberEntity updatedMemberEntity = SiteMemberEntity.builder().memberEntity(beforeUpdatedMemberEntity).nickname(updatedNickname).build();

        given(memberRepository.save(memberEntity)).willReturn(beforeUpdatedMemberEntity);
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(beforeUpdatedMemberEntity));
        given(memberRepository.save(updatedMemberEntity)).willReturn(updatedMemberEntity);
        given(memberRepository.findByNickname(updatedNickname)).willReturn(Optional.of(updatedMemberEntity));

        // when
        memberApplicationService.insert(memberBasicUserInsertRequest);
        SiteMemberResponse updatedMemberResponse = memberApplicationService.update(new SiteMemberUpdateRequest(uuid, updatedNickname, memberEntity.getBirthDate()));

        // then
        assertThat(memberApplicationService.getByNickname(updatedNickname).getFirst()).isEqualTo(updatedMemberResponse);
    }

    @DisplayName("uuid로 회원 제거")
    @Test
    void removeByUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.empty());
        willDoNothing().given(memberRepository).deleteByUuid(uuid);

        // when
        memberApplicationService.insert(memberBasicUserInsertRequest);
        memberApplicationService.removeByUuid(uuid);

        // then
        assertThat(memberApplicationService.getByUuid(uuid)).isEmpty();
    }
}