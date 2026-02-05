package kr.modusplant.framework.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.shared.persistence.compositekey.CommCommentLikeId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_COMMENT_LIKE;

@Entity
@Table(name = COMM_COMMENT_LIKE)
@IdClass(CommCommentLikeId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@ToString
public class CommCommentLikeEntity {
    @Id
    @Column(name = POST_ULID, nullable = false)
    private String postId;

    @Id
    @Column(name = "path", nullable = false)
    private String path;

    @Id
    @Column(name = MEMB_UUID, nullable = false)
    private UUID memberId;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private CommCommentLikeEntity(String postId, String path, UUID memberId) {
        this.postId = postId;
        this.path = path;
        this.memberId = memberId;
    }

    public static CommCommentLikeEntity of(String postId, String path, UUID memberId) {
        return new CommCommentLikeEntity(postId, path, memberId);
    }
}
