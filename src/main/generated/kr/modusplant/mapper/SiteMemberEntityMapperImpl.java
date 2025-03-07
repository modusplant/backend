package kr.modusplant.mapper;

import javax.annotation.processing.Generated;
import kr.modusplant.domain.model.SiteMember;
import kr.modusplant.persistence.entity.SiteMemberEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-28T00:37:32+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class SiteMemberEntityMapperImpl implements SiteMemberEntityMapper {

    @Override
    public SiteMember toSiteMember(SiteMemberEntity siteMemberEntity) {
        if ( siteMemberEntity == null ) {
            return null;
        }

        SiteMember.SiteMemberBuilder siteMember = SiteMember.builder();

        if ( siteMemberEntity.getRole() != null ) {
            siteMember.role( toRole( siteMemberEntity.getRole().name() ) );
        }
        siteMember.uuid( siteMemberEntity.getUuid() );
        siteMember.loggedInAt( siteMemberEntity.getLoggedInAt() );
        siteMember.id( siteMemberEntity.getId() );
        siteMember.pw( siteMemberEntity.getPw() );
        siteMember.name( siteMemberEntity.getName() );
        siteMember.nickname( siteMemberEntity.getNickname() );
        siteMember.email( siteMemberEntity.getEmail() );
        siteMember.sanctionCount( siteMemberEntity.getSanctionCount() );
        siteMember.isDeleted( siteMemberEntity.getIsDeleted() );

        return siteMember.build();
    }
}
