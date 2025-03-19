package kr.modusplant.global.persistence.service;

import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.model.SiteMemberTerm;
import kr.modusplant.global.domain.service.crud.SiteMemberService;
import kr.modusplant.global.domain.service.crud.SiteMemberTermService;
import kr.modusplant.global.mapper.SiteMemberEntityMapper;
import kr.modusplant.global.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.global.mapper.SiteMemberTermEntityMapper;
import kr.modusplant.global.mapper.SiteMemberTermEntityMapperImpl;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import kr.modusplant.global.persistence.repository.SiteMemberTermJpaRepository;
import kr.modusplant.support.context.ServiceOnlyContext;
import kr.modusplant.support.util.domain.SiteMemberTermTestUtils;
import kr.modusplant.support.util.domain.SiteMemberTestUtils;
import kr.modusplant.support.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.support.util.entity.SiteMemberTermEntityTestUtils;
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
class SiteMemberTermServiceImplTest implements SiteMemberTermTestUtils, SiteMemberTermEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberTermService memberTermService;
    private final SiteMemberService memberService;
    private final SiteMemberTermJpaRepository memberTermRepository;
    private final SiteMemberJpaRepository memberRepository;
    private final SiteMemberTermEntityMapper memberTermMapper = new SiteMemberTermEntityMapperImpl();
    private final SiteMemberEntityMapper memberMapper = new SiteMemberEntityMapperImpl();

    @Autowired
    SiteMemberTermServiceImplTest(SiteMemberTermService memberTermService, SiteMemberService memberService, SiteMemberTermJpaRepository memberTermRepository, SiteMemberJpaRepository memberRepository) {
        this.memberTermService = memberTermService;
        this.memberService = memberService;
        this.memberTermRepository = memberTermRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("uuid로 회원 약관 찾기")
    @Test
    void getByUuidTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByUuid(memberTerm.getUuid()).orElseThrow()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedTermsOfUseVersion으로 회원 약관 찾기")
    @Test
    void getByAgreedTermsOfUseVersionTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.empty());
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedTermsOfUseVersion(memberTermEntity.getAgreedTermsOfUseVersion())).willReturn(List.of(memberTermEntity));

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByAgreedTermsOfUseVersion(memberTermEntity.getAgreedTermsOfUseVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedPrivacyPolicyVersion으로 회원 약관 찾기")
    @Test
    void getByOriginalMemberUuidTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.empty());
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedPrivacyPolicyVersion(memberTermEntity.getAgreedPrivacyPolicyVersion())).willReturn(List.of(memberTermEntity));

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByAgreedPrivacyPolicyVersion(memberTermEntity.getAgreedPrivacyPolicyVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedAdInfoReceivingVersion으로 회원 약관 찾기")
    @Test
    void getByEmailTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.empty());
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedAdInfoReceivingVersion(memberTermEntity.getAgreedAdInfoReceivingVersion())).willReturn(List.of(memberTermEntity));

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByAgreedAdInfoReceivingVersion(memberTerm.getAgreedAdInfoReceivingVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("uuid로 회원 약관 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findAll()).willReturn(emptyList());
        willDoNothing().given(memberTermRepository).deleteByUuid(memberTerm.getUuid());

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);
        memberTermService.removeByUuid(memberTerm.getUuid());

        // then
        assertThat(memberTermService.getAll()).isEmpty();
    }
}