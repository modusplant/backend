package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import kr.modusplant.domains.post.framework.out.jpa.entity.AuthorEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.shared.persistence.repository.supers.CreatedAtAndUpdatedAtRepository;
import kr.modusplant.shared.persistence.repository.supers.UlidPrimaryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostJpaRepository extends UlidPrimaryRepository<PostEntity>, CreatedAtAndUpdatedAtRepository<PostEntity>, JpaRepository<PostEntity, String> {
    Page<PostEntity> findByIsPublishedTrueOrderByPublishedAtDesc(Pageable pageable);

    Page<PostEntity> findByPrimaryCategoryAndIsPublishedTrueOrderByPublishedAtDesc(PrimaryCategoryEntity primaryCategory, Pageable pageable);

    Page<PostEntity> findBySecondaryCategoryAndIsPublishedTrueOrderByPublishedAtDesc(SecondaryCategoryEntity secondaryCategory, Pageable pageable);

    Page<PostEntity> findByAuthMemberAndIsPublishedTrueOrderByPublishedAtDesc(AuthorEntity authMember, Pageable pageable);

    Page<PostEntity> findByAuthMemberAndIsPublishedFalseOrderByUpdatedAtDesc(AuthorEntity authMember, Pageable pageable);

    Optional<PostEntity> findByUlid(String ulid);

    @Query(
            value = "SELECT * FROM comm_post p " +
                    "WHERE p.is_published = true AND (" +
                    "p.title ILIKE %:keyword% OR " +
                    "EXISTS (" +
                    "   SELECT 1 FROM jsonb_array_elements(p.content) c " +
                    "   WHERE c->>'type' = 'text' AND c->>'data' ILIKE %:keyword% " +
                    ")) " +
                    "ORDER BY p.published_at desc",
            countQuery = "SELECT COUNT(*) FROM comm_post p " +
                    "WHERE p.is_published = true AND (" +
                    "p.title ILIKE %:keyword% OR " +
                    "EXISTS (" +
                    "   SELECT 1 FROM jsonb_array_elements(p.content) c " +
                    "   WHERE c->>'type' = 'text' AND c->>'data' ILIKE %:keyword% " +
                    ")) " +
                    "ORDER BY p.published_at desc",
            nativeQuery = true
    )
    Page<PostEntity> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE PostEntity t SET t.viewCount = :viewCount WHERE t.ulid = :ulid AND t.viewCount < :viewCount")
    int updateViewCount(@Param("ulid") String ulid, @Param("viewCount") Long viewCount);

}
