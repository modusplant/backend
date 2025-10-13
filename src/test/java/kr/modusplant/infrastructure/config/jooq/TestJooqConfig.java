package kr.modusplant.infrastructure.config.jooq;

import kr.modusplant.infrastructure.config.jdbc.TestDataSourceFactory;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestJooqConfig {

    @Bean
    public DSLContext dsl() {
        return DSL.using(TestDataSourceFactory.getInstance(), SQLDialect.POSTGRES);
    }
}
