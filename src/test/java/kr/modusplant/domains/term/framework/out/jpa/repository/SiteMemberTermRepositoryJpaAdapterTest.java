package kr.modusplant.domains.term.framework.out.jpa.repository;

import kr.modusplant.domains.term.common.util.domain.aggregate.SiteMemberTermTestUtils;
import kr.modusplant.domains.term.domain.aggregate.SiteMemberTerm;
import kr.modusplant.domains.term.framework.out.jpa.mapper.SiteMemberTermJpaMapperImpl;
import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberTermEntityTestUtils;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberTermJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static kr.modusplant.domains.term.common.util.domain.vo.SiteMemberTermIdTestUtils.testSiteMemberTermId;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

class SiteMemberTermRepositoryJpaAdapterTest implements SiteMemberTermTestUtils, SiteMemberEntityTestUtils, SiteMemberTermEntityTestUtils {
    private final SiteMemberJpaRepository siteMemberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final SiteMemberTermJpaMapperImpl siteMemberTermJpaMapper = new SiteMemberTermJpaMapperImpl(siteMemberJpaRepository);
    private final SiteMemberTermJpaRepository siteMemberTermJpaRepository = Mockito.mock(SiteMemberTermJpaRepository.class);
    private final SiteMemberTermRepositoryJpaAdapter siteMemberTermRepositoryJpaAdapter =
            new SiteMemberTermRepositoryJpaAdapter(siteMemberTermJpaMapper, siteMemberTermJpaRepository);

    @Test
    @DisplayName("save로 새로운 사이트 회원 약관 저장")
    void testSave_givenNewSiteMemberTerm_willReturnSavedSiteMemberTerm() {
        // given
        given(siteMemberTermJpaRepository.existsById(MEMBER_TERM_USER_UUID)).willReturn(false);
        given(siteMemberJpaRepository.findById(MEMBER_TERM_USER_UUID)).willReturn(Optional.of(createMemberBasicUserEntityWithUuid()));

        SiteMemberTermEntity savedEntity = Mockito.mock(SiteMemberTermEntity.class);
        given(savedEntity.getUuid()).willReturn(MEMBER_TERM_USER_UUID);
        given(savedEntity.getAgreedTermsOfUseVersion()).willReturn(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
        given(savedEntity.getAgreedPrivacyPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
        given(savedEntity.getAgreedAdInfoReceivingVersion()).willReturn(MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION);
        given(siteMemberTermJpaRepository.save(any())).willReturn(savedEntity);

        // when & then
        assertThat(siteMemberTermRepositoryJpaAdapter.save(createSiteMemberTerm())).isEqualTo(createSiteMemberTerm());
    }

    @Test
    @DisplayName("save로 기존 사이트 회원 약관 수정")
    void testSave_givenExistingSiteMemberTerm_willUpdateVersions() {
        // given
        given(siteMemberTermJpaRepository.existsById(MEMBER_TERM_USER_UUID)).willReturn(true);

        SiteMemberTermEntity existingEntity = Mockito.mock(SiteMemberTermEntity.class);
        given(existingEntity.getUuid()).willReturn(MEMBER_TERM_USER_UUID);
        given(existingEntity.getAgreedTermsOfUseVersion()).willReturn(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
        given(existingEntity.getAgreedPrivacyPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
        given(existingEntity.getAgreedAdInfoReceivingVersion()).willReturn(MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION);
        given(siteMemberTermJpaRepository.getReferenceById(MEMBER_TERM_USER_UUID)).willReturn(existingEntity);

        // when
        SiteMemberTerm result = siteMemberTermRepositoryJpaAdapter.save(createSiteMemberTerm());

        // then
        assertThat(result).isEqualTo(createSiteMemberTerm());
    }

    @Test
    @DisplayName("findById로 사이트 회원 약관 반환(존재할 때)")
    void testFindById_givenValidSiteMemberTermId_willReturnOptionalSiteMemberTerm() {
        // given
        SiteMemberTermEntity entity = Mockito.mock(SiteMemberTermEntity.class);
        given(entity.getUuid()).willReturn(MEMBER_TERM_USER_UUID);
        given(entity.getAgreedTermsOfUseVersion()).willReturn(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
        given(entity.getAgreedPrivacyPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
        given(entity.getAgreedAdInfoReceivingVersion()).willReturn(MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION);
        given(siteMemberTermJpaRepository.findById(MEMBER_TERM_USER_UUID)).willReturn(Optional.of(entity));

        // when & then
        assertThat(siteMemberTermRepositoryJpaAdapter.findById(testSiteMemberTermId)).isEqualTo(Optional.of(createSiteMemberTerm()));
    }

    @Test
    @DisplayName("findById로 빈 Optional 반환(존재하지 않을 때)")
    void testFindById_givenNotFoundSiteMemberTermId_willReturnOptionalEmpty() {
        // given
        given(siteMemberTermJpaRepository.findById(MEMBER_TERM_USER_UUID)).willReturn(Optional.empty());

        // when & then
        assertThat(siteMemberTermRepositoryJpaAdapter.findById(testSiteMemberTermId)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("findAll로 사이트 회원 약관 목록 반환")
    void testFindAll_willReturnSiteMemberTermList() {
        // given
        SiteMemberTermEntity entity = Mockito.mock(SiteMemberTermEntity.class);
        given(entity.getUuid()).willReturn(MEMBER_TERM_USER_UUID);
        given(entity.getAgreedTermsOfUseVersion()).willReturn(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
        given(entity.getAgreedPrivacyPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
        given(entity.getAgreedAdInfoReceivingVersion()).willReturn(MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION);
        given(siteMemberTermJpaRepository.findAll()).willReturn(List.of(entity));

        // when & then
        assertThat(siteMemberTermRepositoryJpaAdapter.findAll()).containsExactly(createSiteMemberTerm());
    }

    @Test
    @DisplayName("isIdExist로 true 반환")
    void testIsIdExist_givenIdThatExists_willReturnTrue() {
        // given
        given(siteMemberTermJpaRepository.existsById(MEMBER_TERM_USER_UUID)).willReturn(true);

        // when & then
        assertThat(siteMemberTermRepositoryJpaAdapter.isIdExist(testSiteMemberTermId)).isEqualTo(true);
    }

    @Test
    @DisplayName("isIdExist로 false 반환")
    void testIsIdExist_givenIdThatIsNotExist_willReturnFalse() {
        // given
        given(siteMemberTermJpaRepository.existsById(MEMBER_TERM_USER_UUID)).willReturn(false);

        // when & then
        assertThat(siteMemberTermRepositoryJpaAdapter.isIdExist(testSiteMemberTermId)).isEqualTo(false);
    }

    @Test
    @DisplayName("deleteById로 사이트 회원 약관 삭제")
    void testDeleteById_givenValidSiteMemberTermId_willDelete() {
        // given
        willDoNothing().given(siteMemberTermJpaRepository).deleteById(MEMBER_TERM_USER_UUID);

        // when & then (예외 없이 실행됨을 확인)
        siteMemberTermRepositoryJpaAdapter.deleteById(testSiteMemberTermId);
    }
}
