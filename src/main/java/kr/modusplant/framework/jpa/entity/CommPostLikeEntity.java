package kr.modusplant.framework.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.shared.persistence.compositekey.CommPostLikeId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_POST_LIKE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_POST_LIKE)
@IdClass(CommPostLikeId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class CommPostLikeEntity {
    @Id
    @Column(name = POST_ULID, nullable = false)
    private String postId;

    @Id
    @Column(name = MEMB_UUID, nullable = false)
    private UUID memberId;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private CommPostLikeEntity(String postId, UUID memberId) {
        this.postId = postId;
        this.memberId = memberId;
    }

    public static CommPostLikeEntity of(String postId, UUID memberId) {
        return new CommPostLikeEntity(postId, memberId);
    }
}
