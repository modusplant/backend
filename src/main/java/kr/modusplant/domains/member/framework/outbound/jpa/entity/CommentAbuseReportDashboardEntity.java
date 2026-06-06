package kr.modusplant.domains.member.framework.outbound.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.member.framework.outbound.jpa.compositekey.CommentAbuseReportDashboardCompositeKey;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_COMMENT_ABUSE_REPORT_DASHBOARD;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_COMMENT_ABUSE_REPORT_DASHBOARD)
@IdClass(CommentAbuseReportDashboardCompositeKey.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class CommentAbuseReportDashboardEntity {
    @Id
    @Column(name = POST_ULID, nullable = false, length = 26)
    private String postUlid;

    @Id
    @Column(name = PATH, nullable = false, columnDefinition = "text")
    private String path;

    @Column(name = "report_count", nullable = false)
    private Integer reportCount;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AbuseReportStatus status;

    @Column(name = "first_reported_at", nullable = false)
    private LocalDateTime firstReportedAt;

    @Column(name = "last_reported_at", nullable = false)
    private LocalDateTime lastReportedAt;

    @Version
    @Column(name = VER_NUM, nullable = false)
    @ToString.Exclude
    private Long versionNumber;

    public String getETagSource() {
        return getPostUlid() + "-" + getPath() + "-" + getVersionNumber();
    }

    public void increaseReportCount() {
        this.reportCount = this.reportCount + 1;
    }

    public void dismiss() {
        this.status = AbuseReportStatus.DISMISSED;
    }

    public void approve() {
        this.status = AbuseReportStatus.BLINDED;
    }

    public void updateLastReportedAt(LocalDateTime lastReportedAt) {
        this.lastReportedAt = lastReportedAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.reportCount == null) {
            this.reportCount = 1;
        }
        if (this.status == null) {
            this.status = AbuseReportStatus.UNCHECKED;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (!(object instanceof CommentAbuseReportDashboardEntity that)) return false;

        return new EqualsBuilder()
                .append(getPostUlid(), that.getPostUlid())
                .append(getPath(), that.getPath())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPostUlid())
                .append(getPath())
                .toHashCode();
    }

    private CommentAbuseReportDashboardEntity(
            String postUlid, String path,
            Integer reportCount, AbuseReportStatus status,
            LocalDateTime firstReportedAt, LocalDateTime lastReportedAt) {
        this.postUlid = postUlid;
        this.path = path;
        this.reportCount = reportCount;
        this.status = status;
        this.firstReportedAt = firstReportedAt;
        this.lastReportedAt = lastReportedAt;
    }

    public static CommentAbuseReportDashboardEntityBuilder builder() {
        return new CommentAbuseReportDashboardEntityBuilder();
    }

    public static final class CommentAbuseReportDashboardEntityBuilder {
        private String postUlid;
        private String path;
        private Integer reportCount;
        private AbuseReportStatus status;
        private LocalDateTime firstReportedAt;
        private LocalDateTime lastReportedAt;

        public CommentAbuseReportDashboardEntityBuilder postUlid(final String postUlid) {
            this.postUlid = postUlid;
            return this;
        }

        public CommentAbuseReportDashboardEntityBuilder path(final String path) {
            this.path = path;
            return this;
        }

        public CommentAbuseReportDashboardEntityBuilder reportCount(final Integer reportCount) {
            this.reportCount = reportCount;
            return this;
        }

        public CommentAbuseReportDashboardEntityBuilder status(final AbuseReportStatus status) {
            this.status = status;
            return this;
        }

        public CommentAbuseReportDashboardEntityBuilder firstReportedAt(final LocalDateTime firstReportedAt) {
            this.firstReportedAt = firstReportedAt;
            return this;
        }

        public CommentAbuseReportDashboardEntityBuilder lastReportedAt(final LocalDateTime lastReportedAt) {
            this.lastReportedAt = lastReportedAt;
            return this;
        }

        public CommentAbuseReportDashboardEntity build() {
            return new CommentAbuseReportDashboardEntity(this.postUlid, this.path, this.reportCount, this.status, this.firstReportedAt, this.lastReportedAt);
        }
    }
}
