package kr.modusplant.framework.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.framework.jpa.generator.UlidGenerator;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_NOTIFICATION;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_NOTIFICATION)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class CommNotificationEntity {

    @Id
    @UlidGenerator
    @Column(nullable = false, updatable = false, length = 26)
    private String ulid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = RECIPIENT_ID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT), nullable = false)
    @ToString.Exclude
    private SiteMemberEntity recipient;

    @Column(name = ACTOR_ID, nullable = false)
    private UUID actorId;

    @Column(name = ACTOR_NICKNAME, length = 16, nullable = false)
    private String actorNickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private NotificationActionType action;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private NotificationStatusType status;

    @Column(name = POST_ULID, length = 26, nullable = false)
    private String postUlid;

    @Column(name = COMMENT_PATH, columnDefinition = "text")
    private String commentPath;

    @Column(name = CONTENT_PREVIEW, length = 100)
    private String contentPreview;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public void read() {
        this.status = NotificationStatusType.READ;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (!(o instanceof CommNotificationEntity that)) return false;
        return new EqualsBuilder().append(getUlid(),that.getUlid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37).append(getUlid()).toHashCode();
    }

    private CommNotificationEntity(String ulid, SiteMemberEntity recipient, UUID actorId, String actorNickname, NotificationActionType action, NotificationStatusType status, String postUlid, String commentPath, String contentPreview) {
        this.ulid = ulid;
        this.recipient = recipient;
        this.actorId = actorId;
        this.actorNickname = actorNickname;
        this.action = action;
        this.status = status;
        this.postUlid = postUlid;
        this.commentPath = commentPath;
        this.contentPreview = contentPreview;
    }

    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static final class NotificationBuilder {
        private String ulid;
        private SiteMemberEntity recipient;
        private UUID actorId;
        private String actorNickname;
        private NotificationActionType action;
        private NotificationStatusType status;
        private String postUlid;
        private String commentPath;
        private String contentPreview;

        public NotificationBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public NotificationBuilder recipient(final SiteMemberEntity recipient) {
            this.recipient = recipient;
            return this;
        }

        public NotificationBuilder actorId(final UUID actorId) {
            this.actorId = actorId;
            return this;
        }

        public NotificationBuilder actorNickname(final String actorNickname) {
            this.actorNickname = actorNickname;
            return this;
        }

        public NotificationBuilder action(final NotificationActionType action) {
            this.action = action;
            return this;
        }

        public NotificationBuilder status(final NotificationStatusType status) {
            this.status = status;
            return this;
        }

        public NotificationBuilder postUlid(final String postUlid) {
            this.postUlid = postUlid;
            return this;
        }

        public NotificationBuilder commentPath(final String commentPath) {
            this.commentPath = commentPath;
            return this;
        }

        public NotificationBuilder contentPreview(final String contentPreview) {
            this.contentPreview = contentPreview;
            return this;
        }

        public NotificationBuilder notification(final CommNotificationEntity commNotificationEntity) {
            this.ulid = commNotificationEntity.ulid;
            this.recipient = commNotificationEntity.recipient;
            this.actorId = commNotificationEntity.actorId;
            this.actorNickname = commNotificationEntity.actorNickname;
            this.action = commNotificationEntity.action;
            this.status = commNotificationEntity.status;
            this.postUlid = commNotificationEntity.postUlid;
            this.commentPath = commNotificationEntity.commentPath;
            this.contentPreview = commNotificationEntity.contentPreview;
            return this;
        }

        public CommNotificationEntity build() {
            return new CommNotificationEntity(this.ulid,this.recipient,this.actorId, this.actorNickname, this.action,this.status,this.postUlid,this.commentPath,this.contentPreview);
        }

    }


}