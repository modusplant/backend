package kr.modusplant.mapper;

import javax.annotation.processing.Generated;
import kr.modusplant.domain.model.SiteMemberAuth;
import kr.modusplant.persistence.entity.SiteMemberAuthEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-28T00:37:32+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class SiteMemberAuthEntityMapperImpl implements SiteMemberAuthEntityMapper {

    @Override
    public SiteMemberAuth toSiteMemberAuth(SiteMemberAuthEntity memberAuthEntity) {
        if ( memberAuthEntity == null ) {
            return null;
        }

        SiteMemberAuth.SiteMemberAuthBuilder siteMemberAuth = SiteMemberAuth.builder();

        siteMemberAuth.uuid( memberAuthEntity.getUuid() );
        siteMemberAuth.hasEmailAuth( memberAuthEntity.getHasEmailAuth() );
        siteMemberAuth.hasGoogleAuth( memberAuthEntity.getHasGoogleAuth() );
        siteMemberAuth.hasKakaoAuth( memberAuthEntity.getHasKakaoAuth() );

        return siteMemberAuth.build();
    }
}
