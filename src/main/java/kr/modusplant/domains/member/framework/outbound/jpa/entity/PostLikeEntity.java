package kr.modusplant.domains.member.framework.outbound.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.framework.outbound.jpa.compositekey.PostLikeCompositeKey;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_POST_LIKE;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_POST_LIKE)
@IdClass(PostLikeCompositeKey.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class PostLikeEntity {
    @Id
    @Column(name = POST_ULID, nullable = false)
    private String postId;

    @Id
    @Column(name = MEMB_UUID, nullable = false)
    private UUID memberId;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private PostLikeEntity(String postId, UUID memberId) {
        this.postId = postId;
        this.memberId = memberId;
    }

    public static PostLikeEntity of(String postId, UUID memberId) {
        return new PostLikeEntity(postId, memberId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostLikeEntity that)) return false;
        return new EqualsBuilder()
                .append(getPostId(), that.getPostId())
                .append(getMemberId(), that.getMemberId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPostId())
                .append(getMemberId())
                .toHashCode();
    }
}
