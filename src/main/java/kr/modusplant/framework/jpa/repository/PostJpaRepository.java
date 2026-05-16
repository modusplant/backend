package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.PostEntity;
import kr.modusplant.shared.persistence.repository.CreatedAtAndUpdatedAtRepository;
import kr.modusplant.shared.persistence.repository.UlidPrimaryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostJpaRepository extends UlidPrimaryRepository<PostEntity>, CreatedAtAndUpdatedAtRepository<PostEntity>, JpaRepository<PostEntity, String> {
    Optional<PostEntity> findByUlid(String ulid);

    @Modifying
    @Query("UPDATE PostEntity t SET t.viewCount = :viewCount WHERE t.ulid = :ulid AND t.viewCount < :viewCount")
    int updateViewCount(@Param("ulid") String ulid, @Param("viewCount") Long viewCount);
}
