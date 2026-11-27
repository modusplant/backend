package kr.modusplant.infrastructure.trace;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.sdk.trace.ReadableSpan;

/* WARN 이상 로그가 기록된 스팬에 로그 수준을 표시하여, OTel Collector의 테일 샘플링이 해당 트레이스를 보존하게 함 */
public class SpanLogLevelMarkingAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    public static final AttributeKey<String> LOG_LEVEL = AttributeKey.stringKey("log.level");

    @Override
    protected void append(ILoggingEvent event) {
        Level level = event.getLevel();
        if (!level.isGreaterOrEqual(Level.WARN)) {
            return;
        }
        Span span = Span.current();
        // 같은 스팬에 더 높은 수준이 이미 표시되었으면 낮은 수준으로 덮어쓰지 않음
        if (span instanceof ReadableSpan readableSpan) {
            String markedLevel = readableSpan.getAttribute(LOG_LEVEL);
            if (markedLevel != null && Level.toLevel(markedLevel).isGreaterOrEqual(level)) {
                return;
            }
        }
        span.setAttribute(LOG_LEVEL, level.toString());
    }
}
