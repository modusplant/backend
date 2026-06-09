package kr.modusplant.infrastructure.config.text;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 * Apache Commons Text 전용 구성입니다.
 */
@Configuration
public class TextConfig {
    @Bean
    public JaroWinklerSimilarity jaroWinklerSimilarity() {
        return new JaroWinklerSimilarity();
    }
}
