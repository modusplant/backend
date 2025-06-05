package kr.modusplant.domains.communication.conversation.domain.service;

import kr.modusplant.domains.communication.conversation.persistence.repository.ConvLikeRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
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
class ConvLikeValidationServiceTest {

    @Mock private ConvPostRepository convPostRepository;
    @Mock private SiteMemberRepository memberRepository;
    @Mock private ConvLikeRepository convLikeRepository;

    @InjectMocks
    private ConvLikeValidationService validationService;

    private final String CONV_POST_ID = "TEST_CONV_POST_ID";
    private final UUID MEMBER_ID = UUID.randomUUID();

    @Test
    @DisplayName("존재하지 않는 게시글일 경우 예외 발생")
    void validateExistedConvPostAndMember_postNotExist() {
        when(convPostRepository.existsById(CONV_POST_ID)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateExistedConvPostAndMember(CONV_POST_ID, MEMBER_ID))
                .isInstanceOf(EntityNotFoundWithUlidException.class);
    }

    @Test
    @DisplayName("존재하지 않는 회원일 경우 예외 발생")
    void validateExistedConvPostAndMember_memberNotExist() {
        when(convPostRepository.existsById(CONV_POST_ID)).thenReturn(true);
        when(memberRepository.existsById(MEMBER_ID)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateExistedConvPostAndMember(CONV_POST_ID, MEMBER_ID))
                .isInstanceOf(EntityExistsWithUuidException.class);
    }

    @Test
    @DisplayName("좋아요가 존재하지 않을 경우 예외 발생")
    void validateConvLikeExists_notLiked() {
        when(convLikeRepository.existsByPostIdAndMemberId(CONV_POST_ID, MEMBER_ID)).thenReturn(false);

        assertThatThrownBy(() -> validationService.validateConvLikeExists(CONV_POST_ID, MEMBER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("member not liked status");
    }

    @Test
    @DisplayName("좋아요가 이미 존재할 경우 예외 발생")
    void validateConvLikeNotExists_alreadyLiked() {
        when(convLikeRepository.existsByPostIdAndMemberId(CONV_POST_ID, MEMBER_ID)).thenReturn(true);

        assertThatThrownBy(() -> validationService.validateConvLikeNotExists(CONV_POST_ID, MEMBER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("member already liked");
    }
}