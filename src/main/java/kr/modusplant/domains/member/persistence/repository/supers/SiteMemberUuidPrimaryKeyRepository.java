package kr.modusplant.domains.member.persistence.repository.supers;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.repository.supers.UuidPrimaryKeyRepository;

import java.util.Optional;

public interface SiteMemberUuidPrimaryKeyRepository<T> extends UuidPrimaryKeyRepository<T> {
    Optional<T> findByMember(SiteMemberEntity member);
}
