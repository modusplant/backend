package kr.modusplant.framework.outbound.config.jpa;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class TransactionMangerConfig {
    private static final int WRITE_TIMEOUT = 30;
    private static final int READ_TIMEOUT = 5;

    @Bean("writeTx")
    @Primary
    public PlatformTransactionManager writeTransactionManager(EntityManagerFactory emf) {
        JpaTransactionManager txManager = new JpaTransactionManager(emf);
        txManager.setDefaultTimeout(WRITE_TIMEOUT);  // 쓰기 트랜잭션 타임아웃
        txManager.setValidateExistingTransaction(true);
        return txManager;
    }

    @Bean("readTx")
    public PlatformTransactionManager readTransactionManager(EntityManagerFactory emf) {
        JpaTransactionManager txManager = new JpaTransactionManager(emf);
        txManager.setDefaultTimeout(READ_TIMEOUT); // 읽기 트랜잭션 타임아웃
        txManager.setValidateExistingTransaction(true);
        return txManager;
    }
}
