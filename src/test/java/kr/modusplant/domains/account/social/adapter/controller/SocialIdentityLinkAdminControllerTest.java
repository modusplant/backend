package kr.modusplant.domains.account.social.adapter.controller;

import kr.modusplant.domains.account.social.usecase.port.repository.SocialIdentityRepository;
import kr.modusplant.shared.exception.ConflictStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.account.shared.kernel.common.util.AccountIdTestUtils.testNormalMemberId;
import static kr.modusplant.domains.account.social.common.util.domain.vo.SocialMemberProfileTestUtils.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SocialIdentityLinkAdminControllerTest {
    private final SocialIdentityRepository socialIdentityRepository = mock(SocialIdentityRepository.class);
    private final SocialIdentityLinkAdminController adminController = 
            new SocialIdentityLinkAdminController(socialIdentityRepository);

    @Nested
    @DisplayName("소셜 연동 데이터 제거 테스트")
    class RemoveSocialLinkTests {
        @Test
        @DisplayName("연동된 카카오 계정을 연동 해제한다")
        void testRemoveSocialLink_givenLinkedKakaoMember_willUnlinkSuccessfully() {
            // given
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testNormalMemberId)).willReturn(testBasicKakaoSocialMemberProfile);

            // when
            adminController.removeSocialLink(testNormalMemberId.getValue());

            // then
            verify(socialIdentityRepository).updateSocialUnlinkedMember(testNormalMemberId);
        }

        @Test
        @DisplayName("일반 계정의 연동 해제 시도 시 예외가 발생한다")
        void testRemoveSocialLink_givenBasicMember_willThrowException() {
            // given
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testNormalMemberId)).willReturn(testBasicSocialMemberProfile);

            // when & then
            assertThatThrownBy(() -> adminController.removeSocialLink(testNormalMemberId.getValue()))
                    .isInstanceOf(ConflictStateException.class);
        }

        @Test
        @DisplayName("소셜 전용 계정의 연동 해제 시도 시 탈퇴 필요 예외가 발생한다")
        void testRemoveSocialLink_givenPureSocialMember_willThrowSocialWithdrawalRequiredException() {
            // given
            given(socialIdentityRepository.getSocialMemberProfileByAccountId(testKakaoAccountId)).willReturn(testKakaoSocialMemberProfile);

            // when & then
            assertThatThrownBy(() -> adminController.removeSocialLink(
                    testKakaoAccountId.getValue())).isInstanceOf(ConflictStateException.class);
        }
    }
}