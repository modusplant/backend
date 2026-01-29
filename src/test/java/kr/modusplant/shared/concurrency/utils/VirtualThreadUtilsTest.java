package kr.modusplant.shared.concurrency.utils;

import kr.modusplant.domains.member.common.util.domain.aggregate.MemberTestUtils;
import kr.modusplant.domains.member.domain.aggregate.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class VirtualThreadUtilsTest implements MemberTestUtils {
    @Test
    @DisplayName("submit으로 Runnable 실행")
    void testSubmit_givenRunnable_willExecuteRunnable() throws ExecutionException, InterruptedException {
        // given
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        // when
        Future<?> result = VirtualThreadUtils.submit(() -> atomicBoolean.set(true));

        // then
        assertThat(result.get()).isNull();
        assertThat(atomicBoolean.get()).isTrue();
    }

    @Test
    @DisplayName("submit으로 Future 타입 값 반환")
    void testSubmit_givenCallable_willReturnFuture() throws ExecutionException, InterruptedException {
        // 원시형 타입
        // given & when
        Future<?> primitiveResult = VirtualThreadUtils.submit(() -> 1);

        // then
        assertThat(primitiveResult.get()).isEqualTo(1);

        // 제네릭 타입
        // given & when
        Future<Member> memberResult = VirtualThreadUtils.submit(this::createMember);

        // then
        assertThat(memberResult.get()).isEqualTo(createMember());
    }

    @Test
    @DisplayName("submitAndGet으로 Runnable 실행")
    void testSubmitAndGet_givenRunnable_willExecuteRunnable() {
        // given
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        // when
        VirtualThreadUtils.submitAndGet(() -> atomicBoolean.set(true));

        // then
        assertThat(atomicBoolean.get()).isTrue();
    }

    @Test
    @DisplayName("submitAndGet으로 제네릭 타입 값 반환")
    void testSubmitAndGet_givenCallable_willReturnGeneric() {
        // 래퍼 클래스 타입
        // given & when
        Integer wrapperResult = VirtualThreadUtils.submitAndGet(() -> 1);

        // then
        assertThat(wrapperResult).isEqualTo(1);

        // 사용자 지정 제네릭 타입
        // given & when
        Member memberResult = VirtualThreadUtils.submitAndGet(this::createMember);

        // then
        assertThat(memberResult).isEqualTo(createMember());
    }
}