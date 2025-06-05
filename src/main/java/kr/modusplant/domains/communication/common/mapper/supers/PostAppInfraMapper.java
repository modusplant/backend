package kr.modusplant.domains.communication.common.mapper.supers;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.mapstruct.Named;

import java.util.UUID;

public interface PostAppInfraMapper {
    @Named("toMemberUuid")
    default UUID toMemberUuid(SiteMemberEntity member) {
        return member.getUuid();
    }

    @Named("toNickname")
    default String toNickname(SiteMemberEntity siteMemberEntity) {
        return siteMemberEntity.getNickname();
    }
}
