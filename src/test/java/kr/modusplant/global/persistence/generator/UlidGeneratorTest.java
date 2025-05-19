package kr.modusplant.global.persistence.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class UlidGeneratorTest {
    private static final UlidGenerator generator = new UlidGenerator();
    private static final Pattern ULID_PATTERN = Pattern.compile("^[0123456789ABCDEFGHJKMNPQRSTVWXYZ]{26}$", Pattern.CASE_INSENSITIVE);

    @Test
    @DisplayName("ULIDGenerator가 올바른 형식의 ULID를 생성하는지 확인")
    void testGenerateUlid() {
        // Given & When
        Object generated = generator.generate(null, null);

        // Then
        assertTrue(generated instanceof String);
        String ulid = (String) generated;
        assertTrue(ULID_PATTERN.matcher(ulid).matches());
    }

    @Test
    @DisplayName("ULIDGenerator가 고유한 값을 생성하는지 확인")
    void testGenerateUniqueUlids() {
        // Given
        int count = 10000;

        // When
        String[] ulids = new String[count];
        for (int i = 0; i < count; i++) {
            ulids[i] = (String) generator.generate(null, null);
        }

        // Then
        long distinctCount = Arrays.stream(ulids).distinct().count();
        assertEquals(count, distinctCount);
    }

    @Test
    @DisplayName("ULID가 시간 순서에 따라 생성되는지 확인")
    void testUlidsAreTimeOrdered() throws InterruptedException {
        // given
        int count = 5;
        List<String> ulids = new ArrayList<>();

        // when
        for (int i = 0; i < count; i++) {
            String ulid = (String) generator.generate(null, null);
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
    @DisplayName("멀티스레드 환경에서 Ulid 생성의 고유성 검증")
    void testMultithreadedUlidGeneration() throws ExecutionException, InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<List<String>>> futures = new ArrayList<>();
        int repeatCount = 1000;
        int ulidCount = 1000;
        Set<String> allUlids = Collections.synchronizedSet(new HashSet<>());

        // when
        for (int i=0; i<repeatCount; i++) {
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

        assertEquals(repeatCount*ulidCount, allUlids.size());
    }

    List<String> generatedUlidList(int count) {
        List<String> ulidList = new ArrayList<>();
        while (count-- > 0) {
            ulidList.add((String) generator.generate(null, null));
        }
        return ulidList;
    }

}