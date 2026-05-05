package kr.modusplant.framework.jpa.generator;

import com.github.f4b6a3.ulid.UlidCreator;
import kr.modusplant.shared.generator.RandomUlidGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class UlidIdGenerator implements RandomUlidGenerator, BeforeExecutionGenerator {
    @Override
    public String generate() {
        return UlidCreator.getMonotonicUlid().toString();
    }

    @Override
    public String generate(SharedSessionContractImplementor var1, Object var2, Object var3, EventType var4) {
        return UlidCreator.getMonotonicUlid().toString();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}
