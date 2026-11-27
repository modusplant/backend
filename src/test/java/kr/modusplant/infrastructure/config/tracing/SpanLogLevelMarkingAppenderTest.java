package kr.modusplant.infrastructure.config.tracing;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import kr.modusplant.infrastructure.trace.SpanLogLevelMarkingAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.infrastructure.trace.SpanLogLevelMarkingAppender.LOG_LEVEL;
import static org.assertj.core.api.Assertions.assertThat;

class SpanLogLevelMarkingAppenderTest {
    private final SdkTracerProvider tracerProvider = SdkTracerProvider.builder().build();
    private final Tracer tracer = tracerProvider.get("test");
    private final LoggerContext loggerContext = new LoggerContext();
    private final SpanLogLevelMarkingAppender appender = createAppender();

    @AfterEach
    void tearDown() {
        appender.stop();
        tracerProvider.close();
    }

    @Test
    @DisplayName("WARN 로그 기록 시 현재 스팬에 WARN 표시 활동 수행")
    void testAppend_givenWarnEvent_willMarkWarn() {
        // given
        Span span = tracer.spanBuilder("span").startSpan();

        // when
        appendWithin(span, Level.WARN);

        // then
        assertThat(((ReadableSpan) span).getAttribute(LOG_LEVEL)).isEqualTo("WARN");
    }

    @Test
    @DisplayName("ERROR 로그 기록 시 현재 스팬에 ERROR 표시 활동 수행")
    void testAppend_givenErrorEvent_willMarkError() {
        // given
        Span span = tracer.spanBuilder("span").startSpan();

        // when
        appendWithin(span, Level.ERROR);

        // then
        assertThat(((ReadableSpan) span).getAttribute(LOG_LEVEL)).isEqualTo("ERROR");
    }

    @Test
    @DisplayName("INFO 로그 기록 시 현재 스팬 표시 생략 활동 수행")
    void testAppend_givenInfoEvent_willSkipMarking() {
        // given
        Span span = tracer.spanBuilder("span").startSpan();

        // when
        appendWithin(span, Level.INFO);

        // then
        assertThat(((ReadableSpan) span).getAttribute(LOG_LEVEL)).isNull();
    }

    @Test
    @DisplayName("ERROR 이후 WARN 로그 기록 시 ERROR 표시 유지 활동 수행")
    void testAppend_givenWarnEventAfterErrorEvent_willKeepError() {
        // given
        Span span = tracer.spanBuilder("span").startSpan();
        appendWithin(span, Level.ERROR);

        // when
        appendWithin(span, Level.WARN);

        // then
        assertThat(((ReadableSpan) span).getAttribute(LOG_LEVEL)).isEqualTo("ERROR");
    }

    @Test
    @DisplayName("WARN 이후 ERROR 로그 기록 시 ERROR로 표시 갱신 활동 수행")
    void testAppend_givenErrorEventAfterWarnEvent_willMarkError() {
        // given
        Span span = tracer.spanBuilder("span").startSpan();
        appendWithin(span, Level.WARN);

        // when
        appendWithin(span, Level.ERROR);

        // then
        assertThat(((ReadableSpan) span).getAttribute(LOG_LEVEL)).isEqualTo("ERROR");
    }

    private SpanLogLevelMarkingAppender createAppender() {
        SpanLogLevelMarkingAppender spanLogLevelMarkingAppender = new SpanLogLevelMarkingAppender();
        spanLogLevelMarkingAppender.setContext(loggerContext);
        spanLogLevelMarkingAppender.start();
        return spanLogLevelMarkingAppender;
    }

    private void appendWithin(Span span, Level level) {
        LoggingEvent event = new LoggingEvent();
        event.setLevel(level);
        event.setMessage("message");
        try (Scope ignored = span.makeCurrent()) {
            appender.doAppend(event);
        }
    }
}
