package kr.modusplant.infrastructure.config.jdbc;

import kr.modusplant.infrastructure.config.exception.ConfigurationException;
import kr.modusplant.infrastructure.config.exception.enums.ConfigurationErrorCode;
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
        ReflectionTestUtils.setField(validator, "apiConnectionSize", 68);
        ReflectionTestUtils.setField(validator, "notificationBulkheadSize", 10);
        ReflectionTestUtils.setField(validator, "adminBulkheadSize", 2);
        ReflectionTestUtils.setField(validator, "allowedConnectionSize", 80);
        ReflectionTestUtils.setField(validator, "maxPoolSize", 90);
    }

    @Test
    @DisplayName("유효한 프로퍼티로 검증 활동 수행")
    void testAfterSingletonsInstantiated_givenValidProperties_willValidateProperties() {
        // given
        given(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).willReturn(100);

        // when & then
        assertThatNoException().isThrownBy(validator::afterSingletonsInstantiated);
    }

    @ParameterizedTest
    @ValueSource(ints = {70, 79})
    @DisplayName("올바르지 않은 allowedConnectionSize로 예외 반환")
    void testAfterSingletonsInstantiated_givenInvalidAllowedConnectionSize_willThrowException(int equalOrUpperValue) {
        // given
        ReflectionTestUtils.setField(validator, "allowedConnectionSize", equalOrUpperValue);

        // when
        ConfigurationException configurationException =
                assertThrows(ConfigurationException.class, validator::afterSingletonsInstantiated);

        // then
        assertThat(configurationException.getErrorCode()).isEqualTo(ConfigurationErrorCode.INCORRECT_RELATIONSHIP_BETWEEN_CONNECTION_SIZE);
        assertThat(configurationException.getMessage()).contains(
                "apiConnectionSize", "notificationBulkheadSize", "adminBulkheadSize", "allowedConnectionSize");
    }

    @ParameterizedTest
    @ValueSource(ints = {70, 80})
    @DisplayName("올바르지 않은 maxPoolSize로 예외 반환")
    void testAfterSingletonsInstantiated_givenInvalidMaxPoolSize_willThrowException(int equalOrLowerValue) {
        // given
        ReflectionTestUtils.setField(validator, "maxPoolSize", equalOrLowerValue);

        // when
        ConfigurationException configurationException =
                assertThrows(ConfigurationException.class, validator::afterSingletonsInstantiated);

        // then
        assertThat(configurationException.getErrorCode()).isEqualTo(ConfigurationErrorCode.INCORRECT_RELATIONSHIP_BETWEEN_CONNECTION_SIZE);
        assertThat(configurationException.getMessage()).contains("allowedConnectionSize", "maxPoolSize");
    }

    @ParameterizedTest
    @ValueSource(ints = {80, 90})
    @DisplayName("올바르지 않은 maxConnections로 예외 반환")
    void testAfterSingletonsInstantiated_givenInvalidMaxConnections_willThrowException(int equalOrLowerValue) {
        // given
        given(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).willReturn(equalOrLowerValue);

        // when
        ConfigurationException configurationException =
                assertThrows(ConfigurationException.class, validator::afterSingletonsInstantiated);

        // then
        assertThat(configurationException.getErrorCode()).isEqualTo(ConfigurationErrorCode.INCORRECT_RELATIONSHIP_BETWEEN_CONNECTION_SIZE);
        assertThat(configurationException.getMessage()).contains("maxPoolSize", "maxConnections");
    }
}
