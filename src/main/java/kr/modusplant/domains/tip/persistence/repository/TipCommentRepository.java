package kr.modusplant.domains.tip.persistence.repository;

import kr.modusplant.domains.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.conversation.persistence.entity.compositekey.ConvCommentCompositeKey;
import kr.modusplant.domains.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.tip.persistence.entity.compositekey.TipCommentCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TipCommentRepository extends JpaRepository<TipCommentEntity, TipCommentCompositeKey> {

    Optional<TipCommentEntity> findById(TipCommentCompositeKey tipCommentCompositeKey);

    List<TipCommentEntity> findBypostUlid(String postUlid);

    List<TipCommentEntity> findByAuthMemberUuid(UUID uuid);

    List<TipCommentEntity> findByCreateMemberUuid(UUID uuid);

    List<TipCommentEntity> findByContent(String content);
}
