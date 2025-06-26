package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.communication.common.error.PostNotFoundException;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostViewCountRedisRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostViewLockRedisRepository;
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
class QnaPostApplicationServiceUnitTest implements SiteMemberEntityTestUtils, QnaCategoryEntityTestUtils, QnaPostEntityTestUtils {
    @Mock
    private QnaPostViewCountRedisRepository qnaPostViewCountRedisRepository;
    @Mock
    private QnaPostRepository qnaPostRepository;
    @Mock
    private QnaPostViewLockRedisRepository qnaPostViewLockRedisRepository;
    @InjectMocks
    private QnaPostApplicationService qnaPostApplicationService;

    private static final UlidIdGenerator generator = new UlidIdGenerator();
    private final String ulid = generator.generate(null,null,null, EventType.INSERT);
    private final UUID memberUuid = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(qnaPostApplicationService, "ttlMinutes", 10L);
    }

    @Test
    @DisplayName("Redis에 조회수값이 있으면 조회")
    void readViewCountShouldReturnRedisValueWhenRedisHasValueTest() {
        // given
        given(qnaPostViewCountRedisRepository.read(ulid)).willReturn(100L);

        // when
        Long result = qnaPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(100L);
        then(qnaPostRepository).should(never()).findByUlid(any());
    }

    @Test
    @DisplayName("Redis에 조회수가 없고 DB에 있으면 DB에서 값을 조회")
    void readViewCountShouldReturnDbValueWhenRedisIsEmptyAndDbHasValueTest() {
        // given
        given(qnaPostViewCountRedisRepository.read(ulid)).willReturn(null);
        QnaPostEntity qnaPostEntity = createQnaPostEntityBuilder()
                .category(createTestQnaCategoryEntity())
                .authMember(createMemberBasicAdminEntityWithUuid())
                .createMember(createMemberBasicAdminEntityWithUuid())
                .viewCount(55L)
                .build();
        given(qnaPostRepository.findByUlid(ulid)).willReturn(Optional.of(qnaPostEntity));

        // when
        Long result = qnaPostApplicationService.readViewCount(ulid);

        // then
        assertThat(result).isEqualTo(55L);
        then(qnaPostViewCountRedisRepository).should().write(ulid,55L);
    }

    @Test
    @DisplayName("Redis와 DB에 모두 조회수값이 없으면 예외 발생")
    void readViewCountShouldThrowExceptionWhenRedisIsEmptyAndDbEmptyTest() {
        // given
        given(qnaPostViewCountRedisRepository.read(ulid)).willReturn(null);
        given(qnaPostRepository.findByUlid(ulid)).willReturn(Optional.empty());

        // when & then
        assertThrows(PostNotFoundException.class,
                () -> qnaPostApplicationService.readViewCount(ulid));
    }

    @Test
    @DisplayName("조회수 락이 걸려있을 때 기존 조회수 가져오기")
    void increaseViewCountWhenLockExistsTest() {
        // given
        when(qnaPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(false);
        when(qnaPostViewCountRedisRepository.read(ulid)).thenReturn(10L);

        // when
        Long result = qnaPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(qnaPostViewLockRedisRepository, times(1)).lock(ulid,memberUuid,10);
        verify(qnaPostViewCountRedisRepository, times(1)).read(ulid);
        verify(qnaPostViewCountRedisRepository,never()).increase(anyString());
        assertThat(result).isEqualTo(10L);
    }

    @Test
    @DisplayName("조회수 락이 걸려있지 않을 때 조회수 증가")
    void increaseViewCountWhenLockNotExistTest() {
        // given
        when(qnaPostViewLockRedisRepository.lock(ulid,memberUuid,10)).thenReturn(true);
        when(qnaPostViewCountRedisRepository.increase(ulid)).thenReturn(11L);

        // when
        Long result = qnaPostApplicationService.increaseViewCount(ulid,memberUuid);

        // then
        verify(qnaPostViewLockRedisRepository,times(1)).lock(ulid,memberUuid,10L);
        verify(qnaPostViewCountRedisRepository,times(1)).increase(ulid);
        verify(qnaPostViewCountRedisRepository,never()).read(ulid);
        assertThat(result).isEqualTo(11L);
    }


}
