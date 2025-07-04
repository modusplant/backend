package kr.modusplant.domains.communication.conversation.app.service;

import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostViewCountRedisRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostViewLockRedisRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.global.persistence.generator.UlidIdGenerator;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConvPostApplicationServiceUnitTest implements SiteMemberEntityTestUtils, ConvCategoryEntityTestUtils, ConvPostEntityTestUtils {
    @Mock
    private ConvPostViewCountRedisRepository convPostViewCountRedisRepository;
    @Mock
    private ConvPostRepository convPostRepository;
    @Mock
    private ConvPostViewLockRedisRepository convPostViewLockRedisRepository;
    @InjectMocks
    private ConvPostApplicationService convPostApplicationService;

    private static final UlidIdGenerator generator = new UlidIdGenerator();
    private final String ulid = generator.generate(null,null,null, EventType.INSERT);
    private final UUID memberUuid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(convPostApplicationService, "ttlMinutes", 10L);
    }

    @Test
    @DisplayName("Redis에 조회수값이 있으면 조회")
    void readViewCountShouldReturnRedisValueWhenRedisHasValueTest() {
        // given
        given(convPostViewCountRedisRepository.read(ulid)).willReturn(100L);

        // when
        Long result = convPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(100L);
        then(convPostRepository).should(never()).findByUlid(any());
    }

    @Test
    @DisplayName("Redis에 조회수가 없고 DB에 있으면 DB에서 값을 조회")
    void readViewCountShouldReturnDbValueWhenRedisIsEmptyAndDbHasValueTest() {
        // given
        given(convPostViewCountRedisRepository.read(ulid)).willReturn(null);
        ConvPostEntity convPostEntity = createConvPostEntityBuilder()
                .category(createTestConvCategoryEntity())
                .authMember(createMemberBasicAdminEntityWithUuid())
                .createMember(createMemberBasicAdminEntityWithUuid())
                .viewCount(55L)
                .build();
        given(convPostRepository.findByUlid(ulid)).willReturn(Optional.of(convPostEntity));

        // when
        Long result = convPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(55L);
        then(convPostViewCountRedisRepository).should().write(ulid,55L);
    }

    @Test
    @DisplayName("Redis와 DB에 모두 조회수값이 없으면 예외 발생")
    void readViewCountShouldThrowExceptionWhenRedisIsEmptyAndDbEmptyTest() {
        // given
        given(convPostViewCountRedisRepository.read(ulid)).willReturn(null);
        given(convPostRepository.findByUlid(ulid)).willReturn(Optional.empty());

        // when & then
        assertThrows(CommunicationNotFoundException.class,
                () -> convPostApplicationService.readViewCount(ulid));
    }

    @Test
    @DisplayName("조회수 락이 걸려있을 때 기존 조회수 가져오기")
    void increaseViewCountWhenLockExistsTest() {
        // given
        when(convPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(false);
        when(convPostViewCountRedisRepository.read(ulid)).thenReturn(10L);

        // when
        Long result = convPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(convPostViewLockRedisRepository, times(1)).lock(ulid,memberUuid,10);
        verify(convPostViewCountRedisRepository, times(1)).read(ulid);
        verify(convPostViewCountRedisRepository,never()).increase(anyString());
        assertThat(result).isEqualTo(10L);
    }

    @Test
    @DisplayName("조회수 락이 걸려있지 않을 때 조회수 증가")
    void increaseViewCountWhenLockNotExistTest() {
        // given
        when(convPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(true);
        when(convPostViewCountRedisRepository.increase(ulid)).thenReturn(11L);

        // when
        Long result = convPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(convPostViewLockRedisRepository,times(1)).lock(ulid,memberUuid,10L);
        verify(convPostViewCountRedisRepository,times(1)).increase(ulid);
        verify(convPostViewCountRedisRepository,never()).read(ulid);
        assertThat(result).isEqualTo(11L);
    }


}
