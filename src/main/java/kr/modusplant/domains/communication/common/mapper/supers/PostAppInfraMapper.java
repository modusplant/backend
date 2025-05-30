package kr.modusplant.domains.communication.common.mapper.supers;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.mapstruct.Named;

public interface PostAppInfraMapper {
    @Named("toNickname")
    default String toNickname(SiteMemberEntity siteMemberEntity) {
        return siteMemberEntity.getNickname();
    }
}
