package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommPostEntity;
import kr.modusplant.framework.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.shared.persistence.repository.supers.CreatedAtAndUpdatedAtRepository;
import kr.modusplant.shared.persistence.repository.supers.UlidPrimaryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommPostJpaRepository extends UlidPrimaryRepository<CommPostEntity>, CreatedAtAndUpdatedAtRepository<CommPostEntity>, JpaRepository<CommPostEntity, String> {

    Page<CommPostEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<CommPostEntity> findByIsPublishedTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<CommPostEntity> findByPrimaryCategoryAndIsPublishedTrueOrderByCreatedAtDesc(CommPrimaryCategoryEntity primaryCategory, Pageable pageable);

    Page<CommPostEntity> findBySecondaryCategoryAndIsPublishedTrueOrderByCreatedAtDesc(CommSecondaryCategoryEntity secondaryCategory, Pageable pageable);

    Page<CommPostEntity> findByAuthMemberAndIsPublishedTrueOrderByCreatedAtDesc(SiteMemberEntity authMember, Pageable pageable);

    Page<CommPostEntity> findByAuthMemberAndIsPublishedTrueOrderByUpdatedAtDesc(SiteMemberEntity authMember, Pageable pageable);

    Optional<CommPostEntity> findByUlid(String ulid);

    Optional<CommPostEntity> findByUlidAndIsPublishedTrue(String ulid);

    long countByIsPublishedTrue();

    @Query(
            value = "SELECT * FROM comm_post p " +
                    "WHERE p.is_published = true AND (" +
                    "p.title ILIKE %:keyword% OR " +
                    "EXISTS (" +
                    "   SELECT 1 FROM jsonb_array_elements(p.content) c " +
                    "   WHERE c->>'type' = 'text' AND c->>'data' ILIKE %:keyword% " +
                    ")) " +
                    "ORDER BY p.created_at desc",
            countQuery = "SELECT COUNT(*) FROM comm_post p " +
                    "WHERE p.is_published = true AND (" +
                    "p.title ILIKE %:keyword% OR " +
                    "EXISTS (" +
                    "   SELECT 1 FROM jsonb_array_elements(p.content) c " +
                    "   WHERE c->>'type' = 'text' AND c->>'data' ILIKE %:keyword% " +
                    ")) " +
                    "ORDER BY p.created_at desc",
            nativeQuery = true
    )
    Page<CommPostEntity> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE CommPostEntity t SET t.viewCount = :viewCount WHERE t.ulid = :ulid AND t.viewCount < :viewCount")
    int updateViewCount(@Param("ulid") String ulid, @Param("viewCount") Long viewCount);
}
