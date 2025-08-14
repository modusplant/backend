package kr.modusplant.legacy.domains.communication.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.legacy.domains.member.vo.MemberUuid.SNAKE_MEMB_UUID;
import static kr.modusplant.shared.database.TableColumnName.CREATED_AT;
import static kr.modusplant.shared.database.TableName.COMM_LIKE;

@Entity
@Table(name = COMM_LIKE)
@IdClass(CommLikeId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CommLikeEntity {
    @Id
    @Column(name = "post_ulid", nullable = false)
    private String postId;

    @Id
    @Column(name = SNAKE_MEMB_UUID, nullable = false)
    private UUID memberId;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private CommLikeEntity(String postId, UUID memberId) {
        this.postId = postId;
        this.memberId = memberId;
    }

    public static CommLikeEntity of(String postId, UUID memberId) {
        return new CommLikeEntity(postId, memberId);
    }
}
