package kr.modusplant.domains.member.framework.outbound.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.framework.outbound.jpa.compositekey.CommentLikeCompositeKey;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_COMMENT_LIKE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_COMMENT_LIKE)
@IdClass(CommentLikeCompositeKey.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@EqualsAndHashCode
public class CommentLikeEntity {
    @Id
    @Column(name = POST_ULID, nullable = false)
    private String postId;

    @Id
    @Column(name = PATH, nullable = false)
    private String path;

    @Id
    @Column(name = MEMB_UUID, nullable = false)
    private UUID memberId;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private CommentLikeEntity(String postId, String path, UUID memberId) {
        this.postId = postId;
        this.path = path;
        this.memberId = memberId;
    }

    public static CommentLikeEntity of(String postId, String path, UUID memberId) {
        return new CommentLikeEntity(postId, path, memberId);
    }
}
