package kr.modusplant.domains.notification.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.notification.common.util.framework.outbound.jpa.entity.FcmTokenEntityTestUtils;
import kr.modusplant.domains.notification.domain.vo.RecipientId;
import kr.modusplant.domains.notification.framework.outbound.jpa.entity.FcmTokenEntity;
import kr.modusplant.domains.notification.framework.outbound.jpa.mapper.supers.FcmTokenJpaMapper;
import kr.modusplant.domains.notification.framework.outbound.jpa.repository.supers.FcmTokenJpaRepository;
import kr.modusplant.shared.enums.Platform;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.notification.common.constant.FcmTokenConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class FcmTokenRepositoryJpaAdapterTest implements FcmTokenEntityTestUtils {
    private final FcmTokenJpaRepository fcmTokenJpaRepository = Mockito.mock(FcmTokenJpaRepository.class);
    private final MemberJpaRepository memberJpaRepository = Mockito.mock(MemberJpaRepository.class);
    private final FcmTokenJpaMapper fcmTokenJpaMapper = Mockito.mock(FcmTokenJpaMapper.class);
    private final FcmTokenRepositoryJpaAdapter fcmTokenRepositoryJpaAdapter = new FcmTokenRepositoryJpaAdapter(fcmTokenJpaRepository, memberJpaRepository, fcmTokenJpaMapper);


    @Nested
    @DisplayName("saveOrUpdate 테스트")
    class SaveOrUpdateTests {

        @Test
        @DisplayName("기존 토큰이 있을 때 갱신 활동 수행")
        void testSaveOrUpdate_givenExistingToken_willProcessAction() {
            // given
            Platform platform = Platform.ANDROID;
            MemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
            FcmTokenEntity existingEntity = createAndroidFcmTokenEntityBuilder().member(memberEntity).build();

            given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
            given(fcmTokenJpaRepository.findByToken(TEST_FCM_TOKEN_ANDROID)).willReturn(Optional.of(existingEntity));

            // when
            fcmTokenRepositoryJpaAdapter.saveOrUpdate(TEST_FCM_TOKEN_ANDROID, MEMBER_BASIC_USER_UUID, platform);

            // then
            assertThat(existingEntity.getMember()).isEqualTo(memberEntity);
            assertThat(existingEntity.getPlatform()).isEqualTo(platform);
            verify(fcmTokenJpaRepository, never()).save(any());
        }

        @Test
        @DisplayName("기존 토큰이 없을 때 신규 저장 활동 수행")
        void testSaveOrUpdate_givenNoExistingToken_willProcessAction() {
            // given
            Platform platform = Platform.WEB;
            MemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
            FcmTokenEntity newEntity = createWebFcmTokenEntityBuilder().member(memberEntity).build();

            given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
            given(fcmTokenJpaRepository.findByToken(TEST_FCM_TOKEN_WEB)).willReturn(Optional.empty());
            given(fcmTokenJpaMapper.toFcmTokenEntity(TEST_FCM_TOKEN_WEB, memberEntity, platform)).willReturn(newEntity);

            // when
            fcmTokenRepositoryJpaAdapter.saveOrUpdate(TEST_FCM_TOKEN_WEB, MEMBER_BASIC_USER_UUID, platform);

            // then
            verify(fcmTokenJpaRepository, times(1)).save(newEntity);
        }

        @Test
        @DisplayName("존재하지 않는 멤버일 때 예외 반환")
        void testSaveOrUpdate_givenInvalidMember_willThrowException() {
            // given
            given(memberJpaRepository.findByUuid(any())).willReturn(Optional.empty());

            // when & then
            assertThrows(NotFoundEntityException.class, () -> fcmTokenRepositoryJpaAdapter.saveOrUpdate(TEST_FCM_TOKEN_IOS, UUID.randomUUID(), Platform.IOS));
        }
    }

    @Nested
    @DisplayName("findTokensByRecipientId 테스트")
    class FindTokensTests {

        @Test
        @DisplayName("수신자 ID로 목록 반환")
        void testFindTokensByRecipientId_givenValidId_willReturnList() {
            // given
            RecipientId recipientId = RecipientId.fromUuid(MEMBER_BASIC_USER_UUID);
            MemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
            FcmTokenEntity tokenEntity1 = createWebFcmTokenEntityBuilder().member(memberEntity).build();
            FcmTokenEntity tokenEntity2 = createIosFcmTokenEntityBuilder().member(memberEntity).build();

            given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
            given(fcmTokenJpaRepository.findAllByMember(memberEntity)).willReturn(List.of(tokenEntity1, tokenEntity2));

            // when
            List<String> result = fcmTokenRepositoryJpaAdapter.findTokensByRecipientId(recipientId);

            // then
            assertThat(result).hasSize(2).containsExactly(TEST_FCM_TOKEN_WEB, TEST_FCM_TOKEN_IOS);
        }
    }

    @Nested
    @DisplayName("deleteByToken 테스트")
    class DeleteTests {

        @Test
        @DisplayName("토큰으로 삭제 활동 수행")
        void testDeleteByToken_givenToken_willProcessAction() {
            // when
            fcmTokenRepositoryJpaAdapter.deleteByToken(TEST_FCM_TOKEN_WEB);

            // then
            verify(fcmTokenJpaRepository, times(1)).deleteByToken(TEST_FCM_TOKEN_WEB);
        }
    }
}