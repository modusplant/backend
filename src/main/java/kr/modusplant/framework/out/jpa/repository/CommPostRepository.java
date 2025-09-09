package kr.modusplant.framework.out.jpa.repository;

import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
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
public interface CommPostRepository extends UlidPrimaryRepository<CommPostEntity>, CreatedAtAndUpdatedAtRepository<CommPostEntity>, JpaRepository<CommPostEntity, String> {

    Page<CommPostEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<CommPostEntity> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<CommPostEntity> findByPrimaryCategoryAndIsDeletedFalseOrderByCreatedAtDesc(CommPrimaryCategoryEntity primaryCategory, Pageable pageable);

    Page<CommPostEntity> findBySecondaryCategoryAndIsDeletedFalseOrderByCreatedAtDesc(CommSecondaryCategoryEntity secondaryCategory, Pageable pageable);

    Page<CommPostEntity> findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(SiteMemberEntity authMember, Pageable pageable);

    Optional<CommPostEntity> findByUlidAndIsDeletedFalse(String ulid);

    @Query(
            value = "SELECT * FROM comm_post p " +
                    "WHERE p.is_deleted = false AND (" +
                    "p.title ILIKE %:keyword% OR " +
                    "EXISTS (" +
                    "   SELECT 1 FROM jsonb_array_elements(p.content) c " +
                    "   WHERE c->>'type' = 'text' AND c->>'data' ILIKE %:keyword% " +
                    ")) " +
                    "ORDER BY p.created_at desc",
            countQuery = "SELECT COUNT(*) FROM comm_post p " +
                    "WHERE p.is_deleted = false AND (" +
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
