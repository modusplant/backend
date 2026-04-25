package kr.modusplant.infrastructure.config.jooq;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.jooq.SpringTransactionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class JooqConfig {

    @Bean
    public DataSourceConnectionProvider connectionProvider(DataSource dataSource) {
        return new DataSourceConnectionProvider(
                new TransactionAwareDataSourceProxy(dataSource)
        );
    }

    @Bean
    public SpringTransactionProvider transactionProvider(PlatformTransactionManager txManager) {
        return new SpringTransactionProvider(txManager);
    }

    @Bean
    public DefaultConfiguration jooqConfiguration(
            DataSourceConnectionProvider connectionProvider,
            SpringTransactionProvider transactionProvider) {
        DefaultConfiguration config = new DefaultConfiguration();
        config.set(connectionProvider);
        config.set(transactionProvider);
        config.set(SQLDialect.POSTGRES);
        return config;
    }

    @Bean
    public DSLContext dslContext(DefaultConfiguration config) {
        return DSL.using(config);
    }
}
