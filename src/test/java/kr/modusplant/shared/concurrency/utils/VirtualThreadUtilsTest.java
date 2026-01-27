package kr.modusplant.shared.concurrency.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class VirtualThreadUtilsTest {
    @Test
    @DisplayName("submit으로 Future 타입 값 반환")
    void testSubmit_givenCallable_willReturnFuture() throws ExecutionException, InterruptedException {
        // given & when
        Future<?> result = VirtualThreadUtils.submit(() -> 1);

        // then
        assertThat(result.get()).isEqualTo(1);
    }
}