package kr.modusplant.legacy.domains.member.app.service;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.framework.out.jpa.entity.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.util.SiteMemberTermEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberTermJpaRepository;
import kr.modusplant.legacy.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberTermUpdateRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberTermResponse;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberTermRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberResponseTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberTermResponseTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.util.VersionUtils.createVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainsServiceWithoutValidationServiceContext
class SiteMemberTermApplicationServiceTest implements SiteMemberTermRequestTestUtils, SiteMemberTermResponseTestUtils, SiteMemberTermEntityTestUtils, SiteMemberRequestTestUtils, SiteMemberResponseTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberTermApplicationService memberTermService;
    private final SiteMemberApplicationService memberService;
    private final SiteMemberTermJpaRepository memberTermRepository;
    private final SiteMemberJpaRepository memberRepository;

    @Autowired
    SiteMemberTermApplicationServiceTest(SiteMemberTermApplicationService memberTermService, SiteMemberApplicationService memberService, SiteMemberTermJpaRepository memberTermRepository, SiteMemberJpaRepository memberRepository) {
        this.memberTermService = memberTermService;
        this.memberService = memberService;
        this.memberTermRepository = memberTermRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("uuid로 회원 약관 얻기")
    @Test
    void getByUuidTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberTermRepository.existsByUuid(uuid)).willReturn(false);
        given(memberTermRepository.findByUuid(uuid)).willReturn(Optional.of(memberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberTermResponse memberTermResponse = memberTermService.insert(memberTermUserInsertRequest);

        // then
        assertThat(memberTermService.getByUuid(uuid).orElseThrow()).isEqualTo(memberTermResponse);
    }

    @DisplayName("member로 회원 약관 얻기")
    @Test
    void getByMemberTest() {
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberTermRepository.existsByUuid(uuid)).willReturn(false);
        given(memberTermRepository.findByUuid(uuid)).willReturn(Optional.of(memberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberTermResponse memberTermResponse = memberTermService.insert(memberTermUserInsertRequest);

        // then
        assertThat(memberTermService.getByMember(memberEntity).orElseThrow()).isEqualTo(memberTermResponse);
    }

    @DisplayName("agreedTermsOfUseVersion으로 회원 약관 얻기")
    @Test
    void getByAgreedTermsOfUseVersionTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberTermRepository.existsByUuid(uuid)).willReturn(false);
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedTermsOfUseVersion(memberTermEntity.getAgreedTermsOfUseVersion())).willReturn(List.of(memberTermEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberTermResponse memberTermResponse = memberTermService.insert(memberTermUserInsertRequest);

        // then
        assertThat(memberTermService.getByAgreedTermsOfUseVersion(memberTermEntity.getAgreedTermsOfUseVersion()).getFirst()).isEqualTo(memberTermResponse);
    }

    @DisplayName("agreedPrivacyPolicyVersion으로 회원 약관 얻기")
    @Test
    void getByOriginalMemberUuidTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberTermRepository.existsByUuid(uuid)).willReturn(false);
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedPrivacyPolicyVersion(memberTermEntity.getAgreedPrivacyPolicyVersion())).willReturn(List.of(memberTermEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberTermResponse memberTermResponse = memberTermService.insert(memberTermUserInsertRequest);

        // then
        assertThat(memberTermService.getByAgreedPrivacyPolicyVersion(memberTermEntity.getAgreedPrivacyPolicyVersion()).getFirst()).isEqualTo(memberTermResponse);
    }

    @DisplayName("agreedAdInfoReceivingVersion으로 회원 약관 얻기")
    @Test
    void getByEmailTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberTermRepository.existsByUuid(uuid)).willReturn(false);
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedAdInfoReceivingVersion(memberTermEntity.getAgreedAdInfoReceivingVersion())).willReturn(List.of(memberTermEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberTermResponse memberTermResponse = memberTermService.insert(memberTermUserInsertRequest);

        // then
        assertThat(memberTermService.getByAgreedAdInfoReceivingVersion(memberTermEntity.getAgreedAdInfoReceivingVersion()).getFirst()).isEqualTo(memberTermResponse);
    }

    @DisplayName("빈 회원 약관 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        UUID uuid = memberEntity.getUuid();

        // getByUuid
        // given & when
        given(memberTermRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(memberTermService.getByUuid(uuid)).isEmpty();

        // getByMember
        // given & when
        given(memberTermRepository.findByMember(memberEntity)).willReturn(Optional.empty());

        // then
        assertThat(memberTermService.getByMember(memberEntity)).isEmpty();
    }

    @DisplayName("회원 약관 갱신")
    @Test
    void updateTest() {
        // given
        String updatedAgreedTermsOfUseVersion = createVersion(1, 0, 1);
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID uuid = memberEntity.getUuid();
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberTermEntity updatedMemberTermEntity = SiteMemberTermEntity.builder().memberTermEntity(memberTermEntity).agreedTermsOfUseVersion(updatedAgreedTermsOfUseVersion).build();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberTermRepository.existsByUuid(uuid)).willReturn(false).willReturn(true);
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity).willReturn(updatedMemberTermEntity);
        given(memberTermRepository.findByUuid(uuid)).willReturn(Optional.of(updatedMemberTermEntity));
        given(memberTermRepository.findByAgreedTermsOfUseVersion(updatedAgreedTermsOfUseVersion)).willReturn(List.of(updatedMemberTermEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        memberTermService.insert(memberTermUserInsertRequest);
        SiteMemberTermResponse updatedMemberTermResponse = memberTermService.update(new SiteMemberTermUpdateRequest(uuid, updatedAgreedTermsOfUseVersion, memberTermEntity.getAgreedPrivacyPolicyVersion(), memberTermEntity.getAgreedAdInfoReceivingVersion()));

        // then
        assertThat(memberTermService.getByAgreedTermsOfUseVersion(updatedAgreedTermsOfUseVersion).getFirst()).isEqualTo(updatedMemberTermResponse);
    }

    @DisplayName("uuid로 회원 약관 제거")
    @Test
    void removeByUuidTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberTermRepository.existsByUuid(uuid)).willReturn(false).willReturn(true);
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByUuid(uuid)).willReturn(Optional.empty());
        willDoNothing().given(memberTermRepository).deleteByUuid(uuid);

        // when
        memberService.insert(memberBasicUserInsertRequest);
        memberTermService.insert(memberTermUserInsertRequest);
        memberTermService.removeByUuid(uuid);

        // then
        assertThat(memberTermService.getByUuid(uuid)).isEmpty();
    }
}