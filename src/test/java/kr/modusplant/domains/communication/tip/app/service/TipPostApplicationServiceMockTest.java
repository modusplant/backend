package kr.modusplant.domains.communication.tip.app.service;

import kr.modusplant.domains.communication.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.group.common.util.entity.PlantGroupEntityTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostViewCountRedisRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostViewLockRedisRepository;
import kr.modusplant.global.error.EntityNotFoundWithUlidException;
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
class TipPostApplicationServiceMockTest implements SiteMemberEntityTestUtils, PlantGroupEntityTestUtils, TipPostEntityTestUtils {
    @Mock
    private TipPostViewCountRedisRepository tipPostViewCountRedisRepository;
    @Mock
    private TipPostRepository tipPostRepository;
    @Mock
    private TipPostViewLockRedisRepository tipPostViewLockRedisRepository;
    @InjectMocks
    private TipPostApplicationService tipPostApplicationService;

    private static final UlidIdGenerator generator = new UlidIdGenerator();
    private final String ulid = generator.generate(null,null,null, EventType.INSERT);
    private final UUID memberUuid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tipPostApplicationService, "ttlMinutes", 10L);
    }

    @Test
    @DisplayName("Redis에 조회수값이 있으면 조회")
    void readViewCountShouldReturnRedisValueWhenRedisHasValueTest() {
        // given
        given(tipPostViewCountRedisRepository.read(ulid)).willReturn(100L);

        // when
        Long result = tipPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(100L);
        then(tipPostRepository).should(never()).findByUlid(any());
    }

    @Test
    @DisplayName("Redis에 조회수가 없고 DB에 있으면 DB에서 값을 조회")
    void readViewCountShouldReturnDbValueWhenRedisIsEmptyAndDbHasValueTest() {
        // given
        given(tipPostViewCountRedisRepository.read(ulid)).willReturn(null);
        TipPostEntity tipPostEntity = createTipPostEntityBuilder()
                .group(createPlantGroupEntity())
                .authMember(createMemberBasicAdminEntityWithUuid())
                .createMember(createMemberBasicAdminEntityWithUuid())
                .viewCount(55L)
                .build();
        given(tipPostRepository.findByUlid(ulid)).willReturn(Optional.of(tipPostEntity));

        // when
        Long result = tipPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(55L);
        then(tipPostViewCountRedisRepository).should().write(ulid,55L);
    }

    @Test
    @DisplayName("Redis와 DB에 모두 조회수값이 없으면 예외 발생")
    void readViewCountShouldThrowExceptionWhenRedisIsEmptyAndDbEmptyTest() {
        // given
        given(tipPostViewCountRedisRepository.read(ulid)).willReturn(null);
        given(tipPostRepository.findByUlid(ulid)).willReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundWithUlidException.class,
                () -> tipPostApplicationService.readViewCount(ulid));
    }

    @Test
    @DisplayName("조회수 락이 걸려있을 때 기존 조회수 가져오기")
    void increaseViewCountWhenLockExistsTest() {
        // given
        when(tipPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(false);
        when(tipPostViewCountRedisRepository.read(ulid)).thenReturn(10L);

        // when
        Long result = tipPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(tipPostViewLockRedisRepository, times(1)).lock(ulid,memberUuid,10);
        verify(tipPostViewCountRedisRepository, times(1)).read(ulid);
        verify(tipPostViewCountRedisRepository,never()).increase(anyString());
        assertThat(result).isEqualTo(10L);
    }

    @Test
    @DisplayName("조회수 락이 걸려있지 않을 때 조회수 증가")
    void increaseViewCountWhenLockNotExistTest() {
        // given
        when(tipPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(true);
        when(tipPostViewCountRedisRepository.increase(ulid)).thenReturn(11L);

        // when
        Long result = tipPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(tipPostViewLockRedisRepository,times(1)).lock(ulid,memberUuid,10L);
        verify(tipPostViewCountRedisRepository,times(1)).increase(ulid);
        verify(tipPostViewCountRedisRepository,never()).read(ulid);
        assertThat(result).isEqualTo(11L);
    }


}
