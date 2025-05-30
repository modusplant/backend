package kr.modusplant.domains.communication.tip.domain.service.like;

import kr.modusplant.domains.communication.tip.persistence.like.repository.TipLikeRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUlidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipLikeValidationServiceTest {

    @Mock private TipPostRepository tipPostRepository;
    @Mock private SiteMemberRepository memberRepository;
    @Mock private TipLikeRepository tipLikeRepository;

    @InjectMocks
    private TipLikeValidationService validationService;

    private final String TIP_POST_ID = "TEST_TIP_POST_ID";
    private final UUID MEMBER_ID = UUID.randomUUID();

    @Test
    @DisplayName("존재하지 않는 게시글일 경우 예외 발생")
    void validateExistedTipPostAndMember_postNotExist() {
        when(tipPostRepository.existsById(TIP_POST_ID)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateExistedTipPostAndMember(TIP_POST_ID, MEMBER_ID))
                .isInstanceOf(EntityNotFoundWithUlidException.class);
    }

    @Test
    @DisplayName("존재하지 않는 회원일 경우 예외 발생")
    void validateExistedTipPostAndMember_memberNotExist() {
        when(tipPostRepository.existsById(TIP_POST_ID)).thenReturn(true);
        when(memberRepository.existsById(MEMBER_ID)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateExistedTipPostAndMember(TIP_POST_ID, MEMBER_ID))
                .isInstanceOf(EntityExistsWithUuidException.class);
    }

    @Test
    @DisplayName("좋아요가 존재하지 않을 경우 예외 발생")
    void validateTipLikeExists_notLiked() {
        when(tipLikeRepository.existsByTipPostIdAndMemberId(TIP_POST_ID, MEMBER_ID)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateTipLikeExists(TIP_POST_ID, MEMBER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("member not liked status");
    }

    @Test
    @DisplayName("좋아요가 이미 존재할 경우 예외 발생")
    void validateTipLikeNotExists_alreadyLiked() {
        when(tipLikeRepository.existsByTipPostIdAndMemberId(TIP_POST_ID, MEMBER_ID)).thenReturn(true);

        assertThatThrownBy(() -> validationService.validateTipLikeNotExists(TIP_POST_ID, MEMBER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("member already liked");
    }
}