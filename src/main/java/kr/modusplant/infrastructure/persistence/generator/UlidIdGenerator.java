package kr.modusplant.infrastructure.persistence.generator;

import com.github.f4b6a3.ulid.UlidCreator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.util.EnumSet;

public class UlidIdGenerator implements BeforeExecutionGenerator {
    @Override
    public String generate(SharedSessionContractImplementor var1, Object var2, Object var3, EventType var4) {
        return UlidCreator.getUlid().toString();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}
