package kr.modusplant.infrastructure.config.jdbc;

import io.micrometer.common.lang.NonNullApi;
import kr.modusplant.infrastructure.config.exception.ConfigurationException;
import kr.modusplant.infrastructure.config.exception.enums.ConfigurationErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@NonNullApi
@Component
@RequiredArgsConstructor
@Getter
public class ConnectionSizePropertyValidator implements SmartInitializingSingleton {

    @Value("${app.semaphore.datasource.allowed-connection-size}")
    private int allowedConnectionSize;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maxPoolSize;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void afterSingletonsInstantiated() {
        if (this.getAllowedConnectionSize() >= this.getMaxPoolSize()) {
            throw new ConfigurationException(ConfigurationErrorCode.INCORRECT_RELATIONSHIP_BETWEEN_CONNECTION_SIZE,
                    new String[]{"allowedConnectionSize", "maxPoolSize"});
        }

        Integer maxConnections = jdbcTemplate.queryForObject("SHOW max_connections;", Integer.class);

        if (maxPoolSize >= requireNonNull(maxConnections).longValue()) {
            throw new ConfigurationException(ConfigurationErrorCode.INCORRECT_RELATIONSHIP_BETWEEN_CONNECTION_SIZE,
                    new String[]{"maxPoolSize", "maxConnections"});
        }
    }
}
