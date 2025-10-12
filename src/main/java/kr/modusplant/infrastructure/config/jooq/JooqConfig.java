package kr.modusplant.infrastructure.config.jooq;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfig {

    @Bean
    public DSLContext dsl(org.jooq.Configuration configuration) {
        return DSL.using(configuration);
    }
}
