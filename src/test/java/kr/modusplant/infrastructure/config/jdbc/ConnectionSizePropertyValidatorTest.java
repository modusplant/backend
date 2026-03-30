package kr.modusplant.infrastructure.config.jdbc;

import kr.modusplant.infrastructure.config.exception.ConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

class ConnectionSizePropertyValidatorTest {
    private final JdbcTemplate jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    private final ConnectionSizePropertyValidator validator = new ConnectionSizePropertyValidator(jdbcTemplate);

    @BeforeEach
    public void beforeEach() {
        ReflectionTestUtils.setField(validator, "allowedConnectionSize", 10);
        ReflectionTestUtils.setField(validator, "maxPoolSize", 11);
    }

    @Test
    @DisplayName("afterSingletonsInstantiated를 통해 프로퍼티 검증")
    void testAfterSingletonsInstantiated_givenCorrectCondition_willDoNothing() {
        // given & when
        given(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).willReturn(12);

        // then
        assertThatNoException().isThrownBy(validator::afterSingletonsInstantiated);
    }

    @ParameterizedTest
    @ValueSource(ints = {9, 10})
    @DisplayName("올바르지 않은 maxPoolSize를 사용하여 validate를 통해 프로퍼티 검증")
    void testValidate_givenMaxPoolSize_willAddFieldError(int equalOrLowerValue) {
        // given
        ReflectionTestUtils.setField(validator, "maxPoolSize", equalOrLowerValue);

        // when
        ConfigurationException configurationException =
                assertThrows(ConfigurationException.class, validator::afterSingletonsInstantiated);

        // then
        assertThat(configurationException.getMessage()).contains("allowedConnectionSize", "maxPoolSize");
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    @DisplayName("올바르지 않은 maxConnections를 사용하여 validate를 통해 프로퍼티 검증")
    void testValidate_givenMaxConnections_willAddFieldError(int equalOrLowerValue) {
        // given
        given(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).willReturn(equalOrLowerValue);

        // when
        ConfigurationException configurationException =
                assertThrows(ConfigurationException.class, validator::afterSingletonsInstantiated);

        // then
        assertThat(configurationException.getMessage()).contains("maxPoolSize", "maxConnections");
    }
}