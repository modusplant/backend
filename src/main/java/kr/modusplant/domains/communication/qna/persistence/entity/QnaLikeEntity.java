package kr.modusplant.domains.communication.qna.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Entity
@Table(name = SNAKE_QNA_LIKE)
@IdClass(QnaLikeId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class QnaLikeEntity {
    @Id
    @Column(name = SNAKE_POST_ULID, nullable = false)
    private String postId;

    @Id
    @Column(name = SNAKE_MEMB_UUID, nullable = false)
    private UUID memberId;

    @Column(name = SNAKE_CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private QnaLikeEntity(String postId, UUID memberId) {
        this.postId = postId;
        this.memberId = memberId;
    }

    public static QnaLikeEntity of(String postId, UUID memberId) {
        return new QnaLikeEntity(postId, memberId);
    }
}
