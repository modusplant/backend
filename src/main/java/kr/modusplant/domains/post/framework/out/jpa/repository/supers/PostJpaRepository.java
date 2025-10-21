package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.persistence.repository.supers.UlidPrimaryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostJpaRepository extends UlidPrimaryRepository<PostEntity>, JpaRepository<PostEntity, String>, PostRepositoryCustom {

    Page<PostEntity> findByAuthMemberAndIsPublishedFalseOrderByUpdatedAtDesc(SiteMemberEntity authMember, Pageable pageable);

    Optional<PostEntity> findByUlid(String ulid);

    @Modifying
    @Query("UPDATE PostEntity t SET t.viewCount = :viewCount WHERE t.ulid = :ulid AND t.viewCount < :viewCount")
    int updateViewCount(@Param("ulid") String ulid, @Param("viewCount") Long viewCount);

}
