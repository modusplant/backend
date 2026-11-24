package kr.modusplant.infrastructure.config.icu4j;

import com.ibm.icu.text.Normalizer2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/***
 * ICU4J 전용 구성입니다.
 */
@Configuration
public class Icu4jConfig {
    public static final String NFD_NORMALIZER_BEAN_NAME = "nfdNormalizer";
    public static final String NFC_NORMALIZER_BEAN_NAME = "nfcNormalizer";
    public static final String NFKD_NORMALIZER_BEAN_NAME = "nfkdNormalizer";
    public static final String NFKC_NORMALIZER_BEAN_NAME = "nfkcNormalizer";

    @Bean(NFD_NORMALIZER_BEAN_NAME)
    public Normalizer2 nfdNormalizer() {
        return Normalizer2.getNFDInstance();
    }

    @Bean(NFC_NORMALIZER_BEAN_NAME)
    public Normalizer2 nfcNormalizer() {
        return Normalizer2.getNFCInstance();
    }

    @Bean(NFKD_NORMALIZER_BEAN_NAME)
    public Normalizer2 nfkdNormalizer() {
        return Normalizer2.getNFKDInstance();
    }

    @Bean(NFKC_NORMALIZER_BEAN_NAME)
    public Normalizer2 nfkcNormalizer() {
        return Normalizer2.getNFKCInstance();
    }
}
