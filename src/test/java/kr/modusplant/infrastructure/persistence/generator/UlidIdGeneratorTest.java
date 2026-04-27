package kr.modusplant.infrastructure.persistence.generator;

import kr.modusplant.framework.jpa.generator.UlidIdGenerator;
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
    @DisplayName("UlidIdGenerator가 올바른 형식의 ULID를 생성")
    class GenerateUlidTest {
        @Test
        @DisplayName("UlidIdGenerator가 generate()로 올바른 형식의 ULID를 생성")
        void generateUlid_givenGenerateWithNoParam_willReturnUlid() {
            // Given & When
            String ulid = generator.generate();

            // Then
            assertTrue(ULID_PATTERN.matcher(ulid).matches());
        }

        @Test
        @DisplayName("UlidIdGenerator가 generate(4개의 매개변수)로 올바른 형식의 ULID를 생성")
        void generateUlid_givenGenerateWithFourParams_willReturnUlid() {
            // Given & When
            String ulid = generator.generate(null, null, null, EventType.INSERT);

            // Then
            assertTrue(ULID_PATTERN.matcher(ulid).matches());
        }
    }

    @Test
    @DisplayName("UlidIdGenerator가 고유한 ULID를 생성")
    void generateUlid_givenValidGenerator_willReturnUniqueUlid() {
        // Given
        int count = 10000;

        // When
        String[] ulids = new String[count];
        for (int i = 0; i < count; i++) {
            ulids[i] = generator.generate(null, null,null,EventType.INSERT);
        }

        // Then
        long distinctCount = Arrays.stream(ulids).distinct().count();
        assertEquals(count, distinctCount);
    }

    @Test
    @DisplayName("UlidIdGenerator가 시간 순서에 따르는 ULID를 생성")
    void generateUlid_givenValidGenerator_willReturnTimeOrderedUlid() throws InterruptedException {
        // given
        int count = 5;
        List<String> ulids = new ArrayList<>();

        // when
        for (int i = 0; i < count; i++) {
            String ulid = generator.generate(null, null,null,EventType.INSERT);
            ulids.add(ulid);
            Thread.sleep(1);
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
    @DisplayName("UlidIdGenerator가 멀티스레드 환경에서도 고유한 ULID를 생성")
    void generateUlidInMultiThread_givenValidGenerator_willReturnUlid() throws ExecutionException, InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<List<String>>> futures = new ArrayList<>();
        int repeatCount = 1000;
        int ulidCount = 1000;
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

    private List<String> generatedUlidList(int count) {
        List<String> ulidList = new ArrayList<>();
        while (count-- > 0) {
            ulidList.add(generator.generate(null, null, null, EventType.INSERT));
        }
        return ulidList;
    }
}