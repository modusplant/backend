package kr.modusplant.infrastructure.config.http;

import io.micrometer.core.instrument.binder.httpcomponents.hc5.PoolingHttpClientConnectionManagerMetricsBinder;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.time.Duration;

/**
 * 아웃바운드 REST 호출(소셜 OAuth 등)이 사용하는 Apache HttpClient 5 커넥션 풀을 명시적으로 구성하고,
 * 해당 풀 상태를 Micrometer(→ Prometheus)로 노출한다.
 *
 * <p>{@code RestClientCustomizer} 를 통해 Auto-Configuration {@code RestClient.Builder} 에
 * 이 풀 기반 요청 팩토리를 주입하므로, {@code GoogleAuthClient}/{@code KakaoAuthClient}
 * 등은 코드 수정 없이 이 풀을 공유한다.
 */
@Configuration
public class HttpClientConfig {

    @Value("${http-client.pool.max-connection-total}")
    private int maxConnectionTotal;

    @Value("${http-client.pool.max-connection-per-route}")
    private int maxConnectionPerRoute;

    @Value("${http-client.pool.connect-timeout}")
    private long connectTimeoutSeconds;

    @Value("${http-client.pool.socket-timeout}")
    private long socketTimeoutSeconds;

    @Value("${http-client.pool.connection-request-timeout}")
    private long connectionRequestTimeoutSeconds;

    @Value("${http-client.pool.idle-eviction}")
    private long idleEvictionSeconds;

    @Bean
    public PoolingHttpClientConnectionManager httpClientConnectionManager() {
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(maxConnectionTotal)
                .setMaxConnPerRoute(maxConnectionPerRoute)
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setConnectTimeout(Timeout.ofSeconds(connectTimeoutSeconds))
                        .setSocketTimeout(Timeout.ofSeconds(socketTimeoutSeconds))
                        .build())
                .build();
    }

    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager httpClientConnectionManager) {
        return HttpClients.custom()
                .setConnectionManager(httpClientConnectionManager)
                .setConnectionManagerShared(true)
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofSeconds(idleEvictionSeconds))
                .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpRequestFactory(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectionRequestTimeout(Duration.ofSeconds(connectionRequestTimeoutSeconds));
        return requestFactory;
    }

    @Bean
    @Order
    public RestClientCustomizer restClientCustomizer(HttpComponentsClientHttpRequestFactory httpRequestFactory) {
        return builder -> builder.requestFactory(httpRequestFactory);
    }

    @Bean
    public PoolingHttpClientConnectionManagerMetricsBinder httpClientConnectionManagerMetricsBinder(
            PoolingHttpClientConnectionManager httpClientConnectionManager) {
        return new PoolingHttpClientConnectionManagerMetricsBinder(httpClientConnectionManager, "hc5-http-client");
    }
}