package kr.modusplant.mapper;

import javax.annotation.processing.Generated;
import kr.modusplant.domain.model.Clause;
import kr.modusplant.persistence.entity.ClauseEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-28T00:37:32+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class ClauseEntityMapperImpl implements ClauseEntityMapper {

    @Override
    public Clause toClause(ClauseEntity clauseEntity) {
        if ( clauseEntity == null ) {
            return null;
        }

        Clause.ClauseBuilder clause = Clause.builder();

        clause.name( clauseEntity.getName() );
        clause.content( clauseEntity.getContent() );

        return clause.build();
    }
}
