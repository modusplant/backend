package kr.modusplant.domains.term.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.term.common.util.domain.aggregate.SiteMemberTermTestUtils;
import kr.modusplant.domains.term.common.util.framework.outbound.jpa.entity.MemberTermEntityTestUtils;
import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.domains.term.framework.outbound.jpa.entity.MemberTermEntity;
import kr.modusplant.domains.term.framework.outbound.jpa.mapper.MemberTermJpaMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.term.common.constant.MemberTermConstant.*;
import static kr.modusplant.domains.term.common.util.domain.vo.SiteMemberTermIdTestUtils.testSiteMemberTermId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class MemberTermRepositoryJpaAdapterTest implements SiteMemberTermTestUtils, MemberEntityTestUtils, MemberTermEntityTestUtils {
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final MemberTermJpaMapperImpl siteMemberTermJpaMapper = new MemberTermJpaMapperImpl(memberJpaRepository);
    private final MemberTermJpaRepository memberTermJpaRepository = Mockito.mock(MemberTermJpaRepository.class);
    private final SiteMemberTermRepositoryJpaAdapter siteMemberTermRepositoryJpaAdapter =
            new SiteMemberTermRepositoryJpaAdapter(siteMemberTermJpaMapper, memberTermJpaRepository);

    @Test
    @DisplayName("save로 새로운 사이트 회원 약관 저장")
    void testSave_givenNewSiteMemberTerm_willReturnSavedSiteMemberTerm() {
        // given
        given(memberTermJpaRepository.existsById(MEMBER_BASIC_USER_UUID)).willReturn(false);
        given(memberJpaRepository.findById(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(createMemberBasicUserEntityWithUuid()));

        MemberTermEntity savedEntity = Mockito.mock(MemberTermEntity.class);
        given(savedEntity.getUuid()).willReturn(MEMBER_BASIC_USER_UUID);
        given(savedEntity.getAgreedTermsOfUseVersion()).willReturn(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
        given(savedEntity.getAgreedPrivacyPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
        given(savedEntity.getAgreedCommunityPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION);
        given(memberTermJpaRepository.save(any())).willReturn(savedEntity);

        // when & then
        assertThat(siteMemberTermRepositoryJpaAdapter.save(createSiteMemberTerm())).isEqualTo(createSiteMemberTerm());
    }

    @Test
    @DisplayName("save로 기존 사이트 회원 약관 수정")
    void testSave_givenExistingSiteMemberTerm_willUpdateVersions() {
        // given
        given(memberTermJpaRepository.existsById(MEMBER_BASIC_USER_UUID)).willReturn(true);

        MemberTermEntity existingEntity = Mockito.mock(MemberTermEntity.class);
        given(existingEntity.getUuid()).willReturn(MEMBER_BASIC_USER_UUID);
        given(existingEntity.getAgreedTermsOfUseVersion()).willReturn(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
        given(existingEntity.getAgreedPrivacyPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
        given(existingEntity.getAgreedCommunityPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION);
        given(memberTermJpaRepository.getReferenceById(MEMBER_BASIC_USER_UUID)).willReturn(existingEntity);

        // when
        SiteMemberTerm result = siteMemberTermRepositoryJpaAdapter.save(createSiteMemberTerm());

        // then
        assertThat(result).isEqualTo(createSiteMemberTerm());
    }

    @Test
    @DisplayName("findById로 사이트 회원 약관 반환(존재할 때)")
    void testFindById_givenValidSiteMemberTermId_willReturnOptionalSiteMemberTerm() {
        // given
        MemberTermEntity entity = Mockito.mock(MemberTermEntity.class);
        given(entity.getUuid()).willReturn(MEMBER_BASIC_USER_UUID);
        given(entity.getAgreedTermsOfUseVersion()).willReturn(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
        given(entity.getAgreedPrivacyPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
        given(entity.getAgreedCommunityPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION);
        given(memberTermJpaRepository.findById(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(entity));

        // when & then
        assertThat(siteMemberTermRepositoryJpaAdapter.findById(testSiteMemberTermId)).isEqualTo(Optional.of(createSiteMemberTerm()));
    }

    @Test
    @DisplayName("findById로 빈 Optional 반환(존재하지 않을 때)")
    void testFindById_givenNotFoundSiteMemberTermId_willReturnOptionalEmpty() {
        // given
        given(memberTermJpaRepository.findById(MEMBER_BASIC_USER_UUID)).willReturn(Optional.empty());

        // when & then
        assertThat(siteMemberTermRepositoryJpaAdapter.findById(testSiteMemberTermId)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("findAll로 사이트 회원 약관 목록 반환")
    void testFindAll_willReturnSiteMemberTermList() {
        // given
        MemberTermEntity entity = Mockito.mock(MemberTermEntity.class);
        given(entity.getUuid()).willReturn(MEMBER_BASIC_USER_UUID);
        given(entity.getAgreedTermsOfUseVersion()).willReturn(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
        given(entity.getAgreedPrivacyPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
        given(entity.getAgreedCommunityPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION);
        given(memberTermJpaRepository.findAll()).willReturn(List.of(entity));

        // when & then
        assertThat(siteMemberTermRepositoryJpaAdapter.findAll()).containsExactly(createSiteMemberTerm());
    }

    @Test
    @DisplayName("isIdExist로 true 반환")
    void testIsIdExist_givenIdThatExists_willReturnTrue() {
        // given
        given(memberTermJpaRepository.existsById(MEMBER_BASIC_USER_UUID)).willReturn(true);

        // when & then
        assertThat(siteMemberTermRepositoryJpaAdapter.isIdExist(testSiteMemberTermId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isIdExist로 false 반환")
    void testIsIdExist_givenIdThatIsNotExist_willReturnFalse() {
        // given
        given(memberTermJpaRepository.existsById(MEMBER_BASIC_USER_UUID)).willReturn(false);

        // when & then
        assertThat(siteMemberTermRepositoryJpaAdapter.isIdExist(testSiteMemberTermId)).isEqualTo(false);
    }

    @Test
    @DisplayName("deleteById로 사이트 회원 약관 삭제")
    void testDeleteById_givenValidSiteMemberTermId_willDelete() {
        // given
        willDoNothing().given(memberTermJpaRepository).deleteById(MEMBER_BASIC_USER_UUID);

        // when & then (예외 없이 실행됨을 확인)
        siteMemberTermRepositoryJpaAdapter.deleteById(testSiteMemberTermId);
    }
}
