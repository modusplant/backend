package kr.modusplant.domains.member.framework.outbound.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.domain.enums.AbuseReportStatus;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.constant.TableColumnName.POST_ULID;
import static kr.modusplant.shared.persistence.constant.TableColumnName.VER_NUM;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_POST_ABUSE_REPORT_DASHBOARD;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_POST_ABUSE_REPORT_DASHBOARD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class PostAbuseReportDashboardEntity {
    @Id
    private String postUlid;

    @MapsId("postUlid")
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = POST_ULID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private PostEntity post;

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
        return getPostUlid() + "-" + getVersionNumber();
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

        if (!(object instanceof PostAbuseReportDashboardEntity that)) return false;

        return new EqualsBuilder()
                .append(getPostUlid(), that.getPostUlid())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getPostUlid()).toHashCode();
    }

    private PostAbuseReportDashboardEntity(PostEntity post, Integer reportCount, AbuseReportStatus status, LocalDateTime firstReportedAt, LocalDateTime lastReportedAt) {
        this.post = post;
        this.reportCount = reportCount;
        this.status = status;
        this.firstReportedAt = firstReportedAt;
        this.lastReportedAt = lastReportedAt;
    }

    public static PostAbuseReportDashboardEntityBuilder builder() {
        return new PostAbuseReportDashboardEntityBuilder();
    }

    public static final class PostAbuseReportDashboardEntityBuilder {
        private PostEntity post;
        private Integer reportCount;
        private AbuseReportStatus status;
        private LocalDateTime firstReportedAt;
        private LocalDateTime lastReportedAt;

        public PostAbuseReportDashboardEntityBuilder post(final PostEntity post) {
            this.post = post;
            return this;
        }

        public PostAbuseReportDashboardEntityBuilder reportCount(final Integer reportCount) {
            this.reportCount = reportCount;
            return this;
        }

        public PostAbuseReportDashboardEntityBuilder status(final AbuseReportStatus status) {
            this.status = status;
            return this;
        }

        public PostAbuseReportDashboardEntityBuilder firstReportedAt(final LocalDateTime firstReportedAt) {
            this.firstReportedAt = firstReportedAt;
            return this;
        }

        public PostAbuseReportDashboardEntityBuilder lastReportedAt(final LocalDateTime lastReportedAt) {
            this.lastReportedAt = lastReportedAt;
            return this;
        }

        public PostAbuseReportDashboardEntityBuilder postAbuseReportDashboard(final PostAbuseReportDashboardEntity dashboard) {
            this.post = dashboard.getPost();
            this.reportCount = dashboard.getReportCount();
            this.status = dashboard.getStatus();
            this.firstReportedAt = dashboard.getFirstReportedAt();
            this.lastReportedAt = dashboard.getLastReportedAt();
            return this;
        }

        public PostAbuseReportDashboardEntity build() {
            return new PostAbuseReportDashboardEntity(this.post, this.reportCount, this.status, this.firstReportedAt, this.lastReportedAt);
        }
    }
}
