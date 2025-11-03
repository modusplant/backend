package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import kr.modusplant.domains.post.usecase.model.PostSummaryReadModel;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom{

    private final EntityManager em;

    @Override
    public Page<PostSummaryReadModel> findByDynamicConditionsAndIsPublishedTrue(UUID primaryCategoryUuid, List<UUID> secondaryCategoryUuids, String keyword, Pageable pageable) {
        return executeQuery(
                null,
                primaryCategoryUuid,
                secondaryCategoryUuids,
                keyword,
                pageable
        );
    }

    @Override
    public Page<PostSummaryReadModel> findByAuthMemberAndDynamicConditionsAndIsPublishedTrue(UUID authMemberUuid, UUID primaryCategoryUuid, List<UUID> secondaryCategoryUuids, String keyword, Pageable pageable) {
        return executeQuery(
                authMemberUuid,
                primaryCategoryUuid,
                secondaryCategoryUuids,
                keyword,
                pageable
        );
    }

    private Page<PostSummaryReadModel> executeQuery(
            UUID memberUuid,
            UUID primaryCategoryUuid,
            List<UUID> secondaryCategoryUuids,
            String keyword,
            Pageable pageable
    ) {
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT " +
                "   p.ulid, " +
                "   pc.category as primaryCategory " +
                "   sc.category as secondaryCategory " +
                "   sm.nickname " +
                "   p.title " +
                "   p.content " +
                "   p.published_at " +
                "FROM comm_post p " +
                "JOIN comm_pri_cate pc ON p.pri_cate_uuid = pc.uuid " +
                "JOIN comm_seco_cate sc ON p.seco_cate_uuid = sc.uuid " +
                "JOIN site_member sm ON p.auth_memb_uuid = sm.uuid " +
                "WHERE p.is_published = true "
        );

        StringBuilder countSqlBuilder = new StringBuilder(
                "SELECT COUNT(*) FROM comm_post p WHERE p.is_published = true"
        );

        Map<String, Object> parameters = new HashMap<>();

        if (memberUuid != null) {
            String memberCondition = " AND p.auth_memb_uuid = :memberUuid";
            sqlBuilder.append(memberCondition);
            countSqlBuilder.append(memberCondition);
            parameters.put("memberUuid", memberUuid);
        }

        if (primaryCategoryUuid != null) {
            String primaryCategoryCondition = " AND p.pri_cate_uuid = :primaryCategoryUuid";
            sqlBuilder.append(primaryCategoryCondition);
            countSqlBuilder.append(primaryCategoryCondition);
            parameters.put("primaryCategoryUuid", primaryCategoryUuid);
        }

        if (secondaryCategoryUuids != null && !secondaryCategoryUuids.isEmpty()) {
            String secondaryCategoryCondition = " AND p.seco_cate_uuid IN (:secondaryCategoryUuids)";
            sqlBuilder.append(secondaryCategoryCondition);
            countSqlBuilder.append(secondaryCategoryCondition);
            parameters.put("secondaryCategoryUuids", secondaryCategoryUuids);
        }

        if (keyword != null && !keyword.isBlank()) {
            String keywordCondition =
                    " AND (p.title ILIKE :keyword OR " +
                    "EXISTS (" +
                    "   SELECT 1 FROM jsonb_array_elements(p.content) c " +
                    "   WHERE c->>'type' = 'text' AND c->>'data' ILIKE :keyword " +
                    ")) ";
            sqlBuilder.append(keywordCondition);
            countSqlBuilder.append(keywordCondition);
            parameters.put("keyword","%"+keyword+"%");
        }

        sqlBuilder.append("ORDER BY p.published_at DESC");

        Query query = em.createNativeQuery(sqlBuilder.toString(), CommPostEntity.class);
        Query countQuery = em.createQuery(countSqlBuilder.toString());

        parameters.forEach((key, value) -> {
            query.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<PostSummaryReadModel> results = query.getResultList();
        Long total = ((Number) countQuery.getSingleResult()).longValue();

        return new PageImpl<>(results, pageable, total);
    }

}
