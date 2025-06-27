package kr.modusplant.domains.communication.qna.domain.service;

import kr.modusplant.domains.communication.common.error.LikeExistsException;
import kr.modusplant.domains.communication.common.error.LikeNotFoundException;
import kr.modusplant.domains.communication.common.error.PostNotFoundException;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaLikeRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityExistsDomainException;
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
class QnaLikeValidationServiceTest {

    @Mock private QnaPostRepository qnaPostRepository;
    @Mock private SiteMemberRepository memberRepository;
    @Mock private QnaLikeRepository qnaLikeRepository;

    @InjectMocks
    private QnaLikeValidationService validationService;

    private final String QNA_POST_ID = "TEST_QNA_POST_ID";
    private final UUID MEMBER_ID = UUID.randomUUID();

    @Test
    @DisplayName("존재하지 않는 게시글일 경우 예외 발생")
    void validateNotFoundQnaPostAndMember_postNotExist() {
        when(qnaPostRepository.existsById(QNA_POST_ID)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateNotFoundQnaPostOrMember(QNA_POST_ID, MEMBER_ID))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 회원일 경우 예외 발생")
    void validateNotFoundQnaPostAndMember_memberNotExist() {
        when(qnaPostRepository.existsById(QNA_POST_ID)).thenReturn(true);
        when(memberRepository.existsById(MEMBER_ID)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateNotFoundQnaPostOrMember(QNA_POST_ID, MEMBER_ID))
                .isInstanceOf(EntityExistsDomainException.class);
    }

    @Test
    @DisplayName("좋아요가 존재하지 않을 경우 예외 발생")
    void validateNotFoundQnaLike_notLiked() {
        when(qnaLikeRepository.existsByPostIdAndMemberId(QNA_POST_ID, MEMBER_ID)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateNotFoundQnaLike(QNA_POST_ID, MEMBER_ID))
                .isInstanceOf(LikeNotFoundException.class);
    }

    @Test
    @DisplayName("좋아요가 이미 존재할 경우 예외 발생")
    void validateExistedQnaLike_alreadyLiked() {
        when(qnaLikeRepository.existsByPostIdAndMemberId(QNA_POST_ID, MEMBER_ID)).thenReturn(true);

        assertThatThrownBy(() -> validationService.validateExistedQnaLike(QNA_POST_ID, MEMBER_ID))
                .isInstanceOf(LikeExistsException.class);
    }
}