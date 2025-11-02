package kr.modusplant.legacy.domains.communication.domain.service;

import kr.modusplant.framework.out.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostLikeJpaRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
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
class CommPostLikeValidationServiceTest {

    @Mock private CommPostJpaRepository commPostRepository;
    @Mock private SiteMemberJpaRepository memberRepository;
    @Mock private CommPostLikeJpaRepository commPostLikeRepository;

    @InjectMocks
    private CommPostLikeValidationService validationService;

    private final String QNA_POST_ID = "TEST_QNA_POST_ID";
    private final UUID MEMBER_ID = UUID.randomUUID();

    @Test
    @DisplayName("존재하지 않는 게시글일 경우 예외 발생")
    void validateNotFoundCommPostAndMember_postNotExist() {
        when(commPostRepository.existsById(QNA_POST_ID)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateNotFoundCommPostOrMember(QNA_POST_ID, MEMBER_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 회원일 경우 예외 발생")
    void validateNotFoundCommPostAndMember_memberNotExist() {
        when(commPostRepository.existsById(QNA_POST_ID)).thenReturn(true);
        when(memberRepository.existsById(MEMBER_ID)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateNotFoundCommPostOrMember(QNA_POST_ID, MEMBER_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("좋아요가 존재하지 않을 경우 예외 발생")
    void validateNotFoundCommPostLike_notLikedPost() {
        when(commPostLikeRepository.existsByPostIdAndMemberId(QNA_POST_ID, MEMBER_ID)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateNotFoundCommPostLike(QNA_POST_ID, MEMBER_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("좋아요가 이미 존재할 경우 예외 발생")
    void validateExistedCommPostLike_alreadyLiked() {
        when(commPostLikeRepository.existsByPostIdAndMemberId(QNA_POST_ID, MEMBER_ID)).thenReturn(true);

        assertThatThrownBy(() -> validationService.validateExistedCommPostLike(QNA_POST_ID, MEMBER_ID))
                .isInstanceOf(EntityExistsException.class);
    }
}