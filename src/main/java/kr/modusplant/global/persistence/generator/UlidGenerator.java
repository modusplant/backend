package kr.modusplant.global.persistence.generator;

import com.github.f4b6a3.ulid.UlidCreator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class UlidGenerator implements BeforeExecutionGenerator, IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return UlidCreator.getUlid().toString();
    }


}
