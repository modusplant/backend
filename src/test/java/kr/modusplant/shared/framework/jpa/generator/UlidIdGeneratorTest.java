package kr.modusplant.shared.framework.jpa.generator;

import org.hibernate.generator.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UlidIdGeneratorTest {
    private static final UlidIdGenerator generator = new UlidIdGenerator();
    private static final Pattern ULID_PATTERN = Pattern.compile("^[0123456789ABCDEFGHJKMNPQRSTVWXYZ]{26}$", Pattern.CASE_INSENSITIVE);

    @Nested
    @DisplayName("generate 메서드 테스트")
    class GenerateTest {
        @Test
        @DisplayName("매개변수 없이 문자열 반환")
        void testGenerate_givenNoParam_willReturnString() {
            // given & when
            String ulid = generator.generate();

            // then
            assertTrue(ULID_PATTERN.matcher(ulid).matches());
        }

        @Test
        @DisplayName("4개 매개변수로 문자열 반환")
        void testGenerate_givenFourParams_willReturnString() {
            // given & when
            String ulid = generator.generate(null, null, null, EventType.INSERT);

            // then
            assertTrue(ULID_PATTERN.matcher(ulid).matches());
        }
    }

    @Test
    @DisplayName("다회 호출 시 문자열 반환")
    void testGenerate_givenManyInvocations_willReturnString() {
        // given
        int count = 10000;

        // when
        String[] ulids = new String[count];
        for (int i = 0; i < count; i++) {
            ulids[i] = generator.generate(null, null, null, EventType.INSERT);
        }

        // then
        long distinctCount = Arrays.stream(ulids).distinct().count();
        assertEquals(count, distinctCount);
    }

    @Test
    @DisplayName("순차 호출 시 문자열 반환")
    void testGenerate_givenSequentialInvocations_willReturnString() {
        // given
        int count = 5;
        List<String> ulids = new ArrayList<>();

        // when
        for (int i = 0; i < count; i++) {
            String ulid = generator.generate(null, null, null, EventType.INSERT);
            ulids.add(ulid);
        }

        // then
        List<String> timeOrderedUlids = new ArrayList<>(ulids);
        Collections.sort(timeOrderedUlids);
        assertEquals(ulids, timeOrderedUlids);

        for (int i = 1; i < count; i++) {
            String prevTimeComponent = ulids.get(i - 1).substring(0, 10);
            String currTimeComponent = ulids.get(i).substring(0, 10);
            assertTrue(currTimeComponent.compareTo(prevTimeComponent) >= 0);
        }
    }

    @Test
    @DisplayName("멀티스레드 호출 시 문자열 반환")
    void testGenerate_givenMultiThreadedInvocations_willReturnString() throws ExecutionException, InterruptedException {
        // given
        int repeatCount;
        int ulidCount;
        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            List<Future<List<String>>> futures = new ArrayList<>();
            repeatCount = 1000;
            ulidCount = 1000;
            Set<String> allUlids = Collections.synchronizedSet(new HashSet<>());

            // when
            for (int i = 0; i < repeatCount; i++) {
                futures.add(executorService.submit(() -> generatedUlidList(ulidCount)));
            }

            // then
            for (Future<List<String>> future : futures) {
                List<String> ulidList = future.get();
                for (String ulid : ulidList) {
                    assertTrue(allUlids.add(ulid));
                }
            }
            executorService.shutdown();
            assertEquals(repeatCount * ulidCount, allUlids.size());
        }
    }

    private List<String> generatedUlidList(int count) {
        List<String> ulidList = new ArrayList<>();
        while (count-- > 0) {
            ulidList.add(generator.generate(null, null, null, EventType.INSERT));
        }
        return ulidList;
    }
}
