package kr.modusplant.domains.notification.framework.out.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.shared.enums.NotificationActionType;
import kr.modusplant.shared.enums.NotificationStatusType;
import kr.modusplant.shared.framework.jpa.generator.UlidGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
public class NotificationEntity {

    @Id
    @UlidGenerator
    @Column(nullable = false, updatable = false, length = 26)
    private String ulid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = RECIPIENT_ID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT), nullable = false)
    @ToString.Exclude
    private MemberEntity recipient;

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
    private LocalDateTime createdAt;

    public void read() {
        this.status = NotificationStatusType.READ;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (!(o instanceof NotificationEntity that)) return false;
        return new EqualsBuilder().append(getUlid(),that.getUlid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37).append(getUlid()).toHashCode();
    }

    private NotificationEntity(String ulid, MemberEntity recipient, UUID actorId, String actorNickname, NotificationActionType action, NotificationStatusType status, String postUlid, String commentPath, String contentPreview, LocalDateTime createdAt) {
        this.ulid = ulid;
        this.recipient = recipient;
        this.actorId = actorId;
        this.actorNickname = actorNickname;
        this.action = action;
        this.status = status;
        this.postUlid = postUlid;
        this.commentPath = commentPath;
        this.contentPreview = contentPreview;
        this.createdAt = createdAt;
    }

    public static NotificationEntityBuilder builder() {
        return new NotificationEntityBuilder();
    }

    public static final class NotificationEntityBuilder {
        private String ulid;
        private MemberEntity recipient;
        private UUID actorId;
        private String actorNickname;
        private NotificationActionType action;
        private NotificationStatusType status;
        private String postUlid;
        private String commentPath;
        private String contentPreview;
        private LocalDateTime createdAt;

        public NotificationEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public NotificationEntityBuilder recipient(final MemberEntity recipient) {
            this.recipient = recipient;
            return this;
        }

        public NotificationEntityBuilder actorId(final UUID actorId) {
            this.actorId = actorId;
            return this;
        }

        public NotificationEntityBuilder actorNickname(final String actorNickname) {
            this.actorNickname = actorNickname;
            return this;
        }

        public NotificationEntityBuilder action(final NotificationActionType action) {
            this.action = action;
            return this;
        }

        public NotificationEntityBuilder status(final NotificationStatusType status) {
            this.status = status;
            return this;
        }

        public NotificationEntityBuilder postUlid(final String postUlid) {
            this.postUlid = postUlid;
            return this;
        }

        public NotificationEntityBuilder commentPath(final String commentPath) {
            this.commentPath = commentPath;
            return this;
        }

        public NotificationEntityBuilder contentPreview(final String contentPreview) {
            this.contentPreview = contentPreview;
            return this;
        }

        public NotificationEntityBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public NotificationEntityBuilder notification(final NotificationEntity notificationEntity) {
            this.ulid = notificationEntity.ulid;
            this.recipient = notificationEntity.recipient;
            this.actorId = notificationEntity.actorId;
            this.actorNickname = notificationEntity.actorNickname;
            this.action = notificationEntity.action;
            this.status = notificationEntity.status;
            this.postUlid = notificationEntity.postUlid;
            this.commentPath = notificationEntity.commentPath;
            this.contentPreview = notificationEntity.contentPreview;
            this.createdAt = notificationEntity.createdAt;
            return this;
        }

        public NotificationEntity build() {
            return new NotificationEntity(this.ulid,this.recipient,this.actorId, this.actorNickname, this.action,this.status,this.postUlid,this.commentPath,this.contentPreview,this.createdAt);
        }

    }


}