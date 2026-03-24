package kr.modusplant.domains.term.framework.out.jpa.mapper;

import kr.modusplant.domains.term.common.util.domain.aggregate.SiteMemberTermTestUtils;
import kr.modusplant.domains.term.domain.exception.SiteMemberNotFoundException;
import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.domains.term.framework.out.jpa.mapper.supers.SiteMemberTermJpaMapper;
import kr.modusplant.framework.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberTermEntityTestUtils;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static kr.modusplant.domains.term.common.util.domain.vo.SiteMemberTermIdTestUtils.testSiteMemberTermId;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberTermConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class SiteMemberTermJpaMapperImplTest implements SiteMemberTermTestUtils, SiteMemberEntityTestUtils, SiteMemberTermEntityTestUtils {
    private final SiteMemberJpaRepository siteMemberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final SiteMemberTermJpaMapper siteMemberTermJpaMapper = new SiteMemberTermJpaMapperImpl(siteMemberJpaRepository);

    @Test
    @DisplayName("toSiteMemberTermNewEntity로 엔터티 반환")
    void testToSiteMemberTermNewEntity_givenValidSiteMemberTerm_willReturnEntity() {
        // given
        given(siteMemberJpaRepository.findById(any())).willReturn(Optional.of(createMemberBasicUserEntityWithUuid()));

        // when
        SiteMemberTermEntity entity = siteMemberTermJpaMapper.toSiteMemberTermNewEntity(createSiteMemberTerm());

        // then
        assertThat(entity.getAgreedTermsOfUseVersion()).isEqualTo(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
        assertThat(entity.getAgreedPrivacyPolicyVersion()).isEqualTo(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
        assertThat(entity.getAgreedAdInfoReceivingVersion()).isEqualTo(MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 toSiteMemberTermNewEntity 호출 시 오류 발생")
    void testToSiteMemberTermNewEntity_givenNotFoundMember_willThrowException() {
        // given
        given(siteMemberJpaRepository.findById(any())).willReturn(Optional.empty());

        // when
        SiteMemberNotFoundException exception = assertThrows(SiteMemberNotFoundException.class,
                () -> siteMemberTermJpaMapper.toSiteMemberTermNewEntity(createSiteMemberTerm()));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.SITE_MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("toSiteMemberTerm으로 사이트 회원 약관 반환")
    void testToSiteMemberTerm_givenValidEntity_willReturnSiteMemberTerm() {
        // given
        SiteMemberTermEntity entity = Mockito.mock(SiteMemberTermEntity.class);
        given(entity.getUuid()).willReturn(MEMBER_TERM_USER_UUID);
        given(entity.getAgreedTermsOfUseVersion()).willReturn(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
        given(entity.getAgreedPrivacyPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
        given(entity.getAgreedAdInfoReceivingVersion()).willReturn(MEMBER_TERM_USER_AGREED_AD_INFO_RECEIVING_VERSION);

        // when & then
        assertThat(siteMemberTermJpaMapper.toSiteMemberTerm(entity)).isEqualTo(createSiteMemberTerm());
    }
}
