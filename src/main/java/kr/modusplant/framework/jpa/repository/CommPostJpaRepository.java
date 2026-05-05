package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtAndUpdatedAtRepository;
import kr.modusplant.shared.persistence.repository.UlidPrimaryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommPostJpaRepository extends UlidPrimaryRepository<CommPostEntity>, CreatedAtAndUpdatedAtRepository<CommPostEntity>, JpaRepository<CommPostEntity, String> {
    Optional<CommPostEntity> findByUlid(String ulid);

    @Modifying
    @Query("UPDATE CommPostEntity t SET t.viewCount = :viewCount WHERE t.ulid = :ulid AND t.viewCount < :viewCount")
    int updateViewCount(@Param("ulid") String ulid, @Param("viewCount") Long viewCount);
}
