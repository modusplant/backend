package kr.modusplant.domains.term.framework.out.jpa.mapper;

import kr.modusplant.domains.member.framework.out.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.term.common.util.domain.aggregate.SiteMemberTermTestUtils;
import kr.modusplant.domains.term.domain.exception.SiteMemberNotFoundException;
import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import kr.modusplant.domains.term.framework.out.jpa.entity.MemberTermEntity;
import kr.modusplant.domains.term.framework.out.jpa.entity.common.util.MemberTermEntityTestUtils;
import kr.modusplant.domains.term.framework.out.jpa.mapper.supers.MemberTermJpaMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.term.common.constant.MemberTermConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class MemberTermJpaMapperImplTest implements SiteMemberTermTestUtils, MemberEntityTestUtils, MemberTermEntityTestUtils {
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final MemberTermJpaMapper memberTermJpaMapper = new MemberTermJpaMapperImpl(memberJpaRepository);

    @Test
    @DisplayName("toMemberTermNewEntity로 엔터티 반환")
    void testToMemberTermNewEntity_givenValidSiteMemberTerm_willReturnEntity() {
        // given
        given(memberJpaRepository.findById(any())).willReturn(Optional.of(createMemberBasicUserEntityWithUuid()));

        // when
        MemberTermEntity entity = memberTermJpaMapper.toMemberTermNewEntity(createSiteMemberTerm());

        // then
        assertThat(entity.getAgreedTermsOfUseVersion()).isEqualTo(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
        assertThat(entity.getAgreedPrivacyPolicyVersion()).isEqualTo(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
        assertThat(entity.getAgreedCommunityPolicyVersion()).isEqualTo(MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 toMemberTermNewEntity 호출 시 오류 발생")
    void testToMemberTermNewEntity_givenNotFoundMember_willThrowException() {
        // given
        given(memberJpaRepository.findById(any())).willReturn(Optional.empty());

        // when
        SiteMemberNotFoundException exception = assertThrows(SiteMemberNotFoundException.class,
                () -> memberTermJpaMapper.toMemberTermNewEntity(createSiteMemberTerm()));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.SITE_MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("toSiteMemberTerm으로 사이트 회원 약관 반환")
    void testToSiteMemberTerm_givenValidEntity_willReturnSiteMemberTerm() {
        // given
        MemberTermEntity entity = Mockito.mock(MemberTermEntity.class);
        given(entity.getUuid()).willReturn(MEMBER_BASIC_USER_UUID);
        given(entity.getAgreedTermsOfUseVersion()).willReturn(MEMBER_TERM_USER_AGREED_TERMS_OF_USE_VERSION);
        given(entity.getAgreedPrivacyPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_PRIVACY_POLICY_VERSION);
        given(entity.getAgreedCommunityPolicyVersion()).willReturn(MEMBER_TERM_USER_AGREED_COMMUNITY_POLICY_VERSION);

        // when & then
        assertThat(memberTermJpaMapper.toSiteMemberTerm(entity)).isEqualTo(createSiteMemberTerm());
    }
}
