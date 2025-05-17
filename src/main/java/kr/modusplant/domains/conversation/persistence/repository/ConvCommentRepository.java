package kr.modusplant.domains.conversation.persistence.repository;

import kr.modusplant.domains.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.conversation.persistence.entity.compositekey.ConvCommentCompositeKey;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConvCommentRepository extends JpaRepository<ConvCommentEntity, ConvCommentCompositeKey> {

    Optional<ConvCommentEntity> findById(ConvCommentCompositeKey convCommentCompositeKey);

    List<ConvCommentEntity> findBypostUlid(String postUlid);

    List<ConvCommentEntity> findByAuthMemberUuid(UUID uuid);

    List<ConvCommentEntity> findByCreateMemberUuid(UUID uuid);

    List<ConvCommentEntity> findByContent(String content);
}
