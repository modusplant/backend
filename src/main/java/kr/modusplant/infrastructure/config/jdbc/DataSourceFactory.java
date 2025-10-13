package kr.modusplant.infrastructure.config.jdbc;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataSourceFactory {

    @Autowired
    private Environment environment;

    @Getter
    private static DataSource instance;

    @PostConstruct
    public void initialize() {
        if (instance == null) {
            String driverClassName = environment.getProperty("spring.datasource.driver-class-name");
            String url = environment.getProperty("spring.datasource.url");
            String username = environment.getProperty("spring.datasource.username");
            String password = environment.getProperty("spring.datasource.password");

            instance = DataSourceBuilder.create()
                    .driverClassName(driverClassName)
                    .url(url)
                    .username(username)
                    .password(password).build();
        }
    }
}