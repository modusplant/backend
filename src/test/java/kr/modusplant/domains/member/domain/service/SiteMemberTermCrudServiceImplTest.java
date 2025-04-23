package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.common.context.DomainServiceOnlyContext;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTermTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberTermEntityTestUtils;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberTerm;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberTermCrudService;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.domains.member.mapper.SiteMemberTermEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberTermEntityMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberCrudJpaRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberTermCrudJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.global.util.VersionUtils.createVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainServiceOnlyContext
class SiteMemberTermCrudServiceImplTest implements SiteMemberTermTestUtils, SiteMemberTermEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberTermCrudService memberTermService;
    private final SiteMemberCrudService memberService;
    private final SiteMemberTermCrudJpaRepository memberTermRepository;
    private final SiteMemberCrudJpaRepository memberRepository;
    private final SiteMemberTermEntityMapper memberTermMapper = new SiteMemberTermEntityMapperImpl();
    private final SiteMemberEntityMapper memberMapper = new SiteMemberEntityMapperImpl();

    @Autowired
    SiteMemberTermCrudServiceImplTest(SiteMemberTermCrudService memberTermService, SiteMemberCrudService memberService, SiteMemberTermCrudJpaRepository memberTermRepository, SiteMemberCrudJpaRepository memberRepository) {
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
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.of(memberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByUuid(memberTerm.getUuid()).orElseThrow()).isEqualTo(memberTerm);
    }

    @DisplayName("member로 회원 약관 얻기")
    @Test
    void getByMemberTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.of(memberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByMember(member).orElseThrow()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedTermsOfUseVersion으로 회원 약관 얻기")
    @Test
    void getByAgreedTermsOfUseVersionTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.of(memberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedTermsOfUseVersion(memberTermEntity.getAgreedTermsOfUseVersion())).willReturn(List.of(memberTermEntity));

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByAgreedTermsOfUseVersion(memberTermEntity.getAgreedTermsOfUseVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedPrivacyPolicyVersion으로 회원 약관 얻기")
    @Test
    void getByOriginalMemberUuidTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.of(memberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedPrivacyPolicyVersion(memberTermEntity.getAgreedPrivacyPolicyVersion())).willReturn(List.of(memberTermEntity));

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByAgreedPrivacyPolicyVersion(memberTermEntity.getAgreedPrivacyPolicyVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedAdInfoReceivingVersion으로 회원 약관 얻기")
    @Test
    void getByEmailTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.of(memberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedAdInfoReceivingVersion(memberTermEntity.getAgreedAdInfoReceivingVersion())).willReturn(List.of(memberTermEntity));

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByAgreedAdInfoReceivingVersion(memberTerm.getAgreedAdInfoReceivingVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("빈 회원 약관 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        UUID uuid = memberEntity.getUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        // getByUuid
        // given & when
        given(memberTermRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(memberTermService.getByUuid(uuid)).isEmpty();

        // getByMember
        // given & when
        given(memberTermRepository.findByMember(memberEntity)).willReturn(Optional.empty());

        // then
        assertThat(memberTermService.getByMember(member)).isEmpty();
    }

    @DisplayName("회원 약관 갱신")
    @Test
    void updateTest() {
        // given
        String updatedAgreedTermsOfUseVersion = createVersion(1, 0, 1);
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberTermEntity updatedMemberTermEntity = SiteMemberTermEntity.builder().memberTermEntity(memberTermEntity).agreedTermsOfUseVersion(updatedAgreedTermsOfUseVersion).build();
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);
        SiteMemberTerm updatedMemberTerm = memberTermMapper.toSiteMemberTerm(updatedMemberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.of(memberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedTermsOfUseVersion(updatedAgreedTermsOfUseVersion)).willReturn(List.of(updatedMemberTermEntity));

        // when
        memberService.insert(member);
        memberTermService.insert(memberTerm);
        memberTermService.update(updatedMemberTerm);

        // then
        assertThat(memberTermService.getByAgreedTermsOfUseVersion(updatedAgreedTermsOfUseVersion).getFirst()).isEqualTo(updatedMemberTerm);
    }

    @DisplayName("uuid로 회원 약관 제거")
    @Test
    void removeByUuidTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);
        UUID uuid = memberTerm.getUuid();

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.empty());
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        willDoNothing().given(memberTermRepository).deleteByUuid(uuid);

        // when
        memberService.insert(member);
        memberTermService.insert(memberTerm);
        memberTermService.removeByUuid(uuid);

        // then
        assertThat(memberTermService.getByUuid(uuid)).isEmpty();
    }
}