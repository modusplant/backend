package kr.modusplant.infrastructure.config;

import io.pyroscope.http.Format;
import io.pyroscope.javaagent.EventType;
import io.pyroscope.javaagent.PyroscopeAgent;
import io.pyroscope.javaagent.config.Config;
import io.pyroscope.javaagent.config.ProfilerType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "pyroscope.enabled", havingValue = "true")
public class ProfilingConfig {

    @Value("${pyroscope.server-address}")
    private String serverAddress;

    @Value("${pyroscope.application-name}")
    private String applicationName;

    @PostConstruct
    public void init() {

        Config.Builder builder = new Config.Builder()
                .setApplicationName(applicationName)
                .setFormat(Format.JFR)
                .setProfilerType(ProfilerType.JFR)
                .setServerAddress(serverAddress);

        builder.setProfilingEvent(EventType.ITIMER);

        PyroscopeAgent.start(builder.build());
    }
}