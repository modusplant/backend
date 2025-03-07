package kr.modusplant.mapper;

import javax.annotation.processing.Generated;
import kr.modusplant.domain.model.SiteMemberClause;
import kr.modusplant.persistence.entity.SiteMemberClauseEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-28T00:37:32+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class SiteMemberClauseEntityMapperImpl implements SiteMemberClauseEntityMapper {

    @Override
    public SiteMemberClause toSiteMemberClause(SiteMemberClauseEntity memberClauseEntity) {
        if ( memberClauseEntity == null ) {
            return null;
        }

        SiteMemberClause.SiteMemberClauseBuilder siteMemberClause = SiteMemberClause.builder();

        siteMemberClause.uuid( memberClauseEntity.getUuid() );
        siteMemberClause.agreedTermsOfUseVersion( memberClauseEntity.getAgreedTermsOfUseVersion() );
        siteMemberClause.agreedPrivacyPolicyVersion( memberClauseEntity.getAgreedPrivacyPolicyVersion() );
        siteMemberClause.agreedAdInfoReceivingVersion( memberClauseEntity.getAgreedAdInfoReceivingVersion() );

        return siteMemberClause.build();
    }
}
