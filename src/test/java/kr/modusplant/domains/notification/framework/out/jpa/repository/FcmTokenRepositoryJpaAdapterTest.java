package kr.modusplant.domains.notification.framework.out.jpa.repository;

import kr.modusplant.domains.notification.domain.vo.RecipientId;
import kr.modusplant.domains.notification.framework.out.jpa.mapper.supers.FcmTokenJpaMapper;
import kr.modusplant.domains.notification.framework.out.jpa.repository.supers.FcmTokenJpaRepository;
import kr.modusplant.framework.jpa.entity.FcmTokenEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.common.util.FcmTokenEntityTestUtils;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.shared.enums.Platform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.util.constant.FcmTokenConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class FcmTokenRepositoryJpaAdapterTest implements FcmTokenEntityTestUtils {
    private final FcmTokenJpaRepository fcmTokenJpaRepository = Mockito.mock(FcmTokenJpaRepository.class);
    private final SiteMemberJpaRepository memberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final FcmTokenJpaMapper fcmTokenJpaMapper = Mockito.mock(FcmTokenJpaMapper.class);
    private final FcmTokenRepositoryJpaAdapter fcmTokenRepositoryJpaAdapter = new FcmTokenRepositoryJpaAdapter(fcmTokenJpaRepository, memberJpaRepository, fcmTokenJpaMapper);


    @Nested
    @DisplayName("saveOrUpdate 테스트")
    class SaveOrUpdateTests {

        @Test
        @DisplayName("기존에 등록된 토큰이 있으면 멤버와 플랫폼 정보를 업데이트")
        void testSaveOrUpdate_whenTokenExists_willUpdateEntity() {
            // given
            Platform platform = Platform.ANDROID;
            SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
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
        @DisplayName("기존에 등록된 토큰이 없으면 새로운 토큰 엔티티를 저장한다")
        void testSaveOrUpdate_whenTokenNotExists_willSaveNewEntity() {
            // given
            Platform platform = Platform.WEB;
            SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
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
        @DisplayName("존재하지 않는 멤버 UUID로 요청 시 NotFoundEntityException이 발생한다")
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
        @DisplayName("수신자 ID로 등록된 모든 토큰 문자열 리스트를 반환한다")
        void testFindTokensByRecipientId_givenValidId_willReturnTokenList() {
            // given
            RecipientId recipientId = RecipientId.fromUuid(MEMBER_BASIC_USER_UUID);
            SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
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
        @DisplayName("토큰 문자열로 해당 토큰 정보를 삭제한다")
        void testDeleteByToken_willCallDelete() {
            // when
            fcmTokenRepositoryJpaAdapter.deleteByToken(TEST_FCM_TOKEN_WEB);

            // then
            verify(fcmTokenJpaRepository, times(1)).deleteByToken(TEST_FCM_TOKEN_WEB);
        }
    }
}