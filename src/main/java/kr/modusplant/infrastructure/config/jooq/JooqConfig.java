package kr.modusplant.infrastructure.config.jooq;

import kr.modusplant.infrastructure.config.jdbc.DataSourceFactory;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {

    @Bean
    public DSLContext dsl() {
        return DSL.using(DataSourceFactory.getInstance(), SQLDialect.POSTGRES);
    }
}
