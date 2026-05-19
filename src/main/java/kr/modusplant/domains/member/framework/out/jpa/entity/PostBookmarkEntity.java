package kr.modusplant.domains.member.framework.out.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.framework.out.jpa.compositekey.PostBookmarkCompositeKey;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_POST_BOOKMARK;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_POST_BOOKMARK)
@IdClass(PostBookmarkCompositeKey.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@EqualsAndHashCode
public class PostBookmarkEntity {
    @Id
    @Column(name = POST_ULID, nullable = false)
    private String postId;

    @Id
    @Column(name = MEMB_UUID, nullable = false)
    private UUID memberId;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private PostBookmarkEntity(String postId, UUID memberId) {
        this.postId = postId;
        this.memberId = memberId;
    }

    public static PostBookmarkEntity of(String postId, UUID memberId) {
        return new PostBookmarkEntity(postId, memberId);
    }
}
