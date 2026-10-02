package kr.modusplant.shared.framework.jackson.holder;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.shared.exception.NotInitializedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.HOLDER_NOT_INITIALIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObjectMapperHolderTest {
    private static final String STATIC_OBJECT_MAPPER = "staticObjectMapper";

    private Object originalStaticObjectMapper;

    @BeforeEach
    void saveStaticObjectMapper() {
        originalStaticObjectMapper = ReflectionTestUtils.getField(ObjectMapperHolder.class, STATIC_OBJECT_MAPPER);
    }

    @AfterEach
    void restoreStaticObjectMapper() {
        ReflectionTestUtils.setField(ObjectMapperHolder.class, STATIC_OBJECT_MAPPER, originalStaticObjectMapper);
    }

    @Test
    @DisplayName("초기화된 홀더로 ObjectMapper 반환")
    public void testGetStaticObjectMapper_givenInitializedHolder_willReturnObjectMapper() {
        // given
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        new ObjectMapperHolder(objectMapper);

        // when
        ObjectMapper result = ObjectMapperHolder.getStaticObjectMapper();

        // then
        assertThat(result).isSameAs(objectMapper);
    }

    @Test
    @DisplayName("초기화되지 않은 홀더로 예외 반환")
    public void testGetStaticObjectMapper_givenUninitializedHolder_willThrowException() {
        // given
        ReflectionTestUtils.setField(ObjectMapperHolder.class, STATIC_OBJECT_MAPPER, null);

        // when
        NotInitializedException exception = assertThrows(NotInitializedException.class, ObjectMapperHolder::getStaticObjectMapper);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(HOLDER_NOT_INITIALIZED);
    }
}
