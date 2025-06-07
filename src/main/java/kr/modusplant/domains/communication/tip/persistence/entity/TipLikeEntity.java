package kr.modusplant.domains.communication.tip.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.domains.member.vo.MemberUuid.SNAKE_MEMB_UUID;
import static kr.modusplant.global.vo.SnakeCaseWord.SNAKE_CREATED_AT;
import static kr.modusplant.global.vo.TableName.TIP_LIKE;

@Entity
@Table(name = TIP_LIKE)
@IdClass(TipLikeId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class TipLikeEntity {
    @Id
    @Column(name = "post_ulid", nullable = false)
    private String postId;

    @Id
    @Column(name = SNAKE_MEMB_UUID, nullable = false)
    private UUID memberId;

    @Column(name = SNAKE_CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private TipLikeEntity(String postId, UUID memberId) {
        this.postId = postId;
        this.memberId = memberId;
    }

    public static TipLikeEntity of(String postId, UUID memberId) {
        return new TipLikeEntity(postId, memberId);
    }
}
