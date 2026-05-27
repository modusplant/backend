package kr.modusplant.domains.member.framework.outbound.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.record.FilenameAndSrcEntityRecord;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.PROP_BUG_REP;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = PROP_BUG_REP)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class ProposalOrBugReportEntity {
    @Id
    @Column(nullable = false, updatable = false)
    private String ulid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = MEMB_UUID, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private MemberEntity member;

    @Column(nullable = false, updatable = false, length = 60)
    private String title;

    @Column(nullable = false, updatable = false, length = 600)
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(updatable = false)
    @ToString.Exclude
    private List<FilenameAndSrcEntityRecord> image;

    @Column(name = "image_number", updatable = false)
    private Integer imageNumber;

    @Column(name = CHECKED_AT)
    private LocalDateTime checkedAt;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public void check() {
        this.checkedAt = LocalDateTime.now();
    }

    public String getETagSource() {
        return getUlid() + "-" + getCheckedAt();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (!(object instanceof ProposalOrBugReportEntity that)) return false;

        return new EqualsBuilder().append(getUlid(), that.getUlid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getUlid()).toHashCode();
    }

    private ProposalOrBugReportEntity(String ulid,
                                      MemberEntity member,
                                      String title,
                                      String content,
                                      List<FilenameAndSrcEntityRecord> image,
                                      Integer imageNumber,
                                      LocalDateTime checkedAt) {
        this.ulid = ulid;
        this.member = member;
        this.title = title;
        this.content = content;
        this.image = image;
        this.imageNumber = imageNumber;
        this.checkedAt = checkedAt;
    }

    public static ProposalOrBugReportEntityBuilder builder() {
        return new ProposalOrBugReportEntityBuilder();
    }

    public static final class ProposalOrBugReportEntityBuilder {
        private String ulid;
        private MemberEntity member;
        private String title;
        private String content;
        private List<FilenameAndSrcEntityRecord> image;
        private Integer imageNumber;
        private LocalDateTime checkedAt;

        public ProposalOrBugReportEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public ProposalOrBugReportEntityBuilder member(final MemberEntity member) {
            this.member = member;
            return this;
        }

        public ProposalOrBugReportEntityBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public ProposalOrBugReportEntityBuilder content(final String content) {
            this.content = content;
            return this;
        }

        public ProposalOrBugReportEntityBuilder image(final List<FilenameAndSrcEntityRecord> image) {
            this.image = image;
            return this;
        }

        public ProposalOrBugReportEntityBuilder imageNumber(final Integer imageNumber) {
            this.imageNumber = imageNumber;
            return this;
        }

        public ProposalOrBugReportEntityBuilder checkedAt(final LocalDateTime checkedAt) {
            this.checkedAt = checkedAt;
            return this;
        }

        public ProposalOrBugReportEntityBuilder proposalBugReport(final ProposalOrBugReportEntity propBugRep) {
            this.ulid = propBugRep.getUlid();
            this.member = propBugRep.getMember();
            this.title = propBugRep.getTitle();
            this.content = propBugRep.getContent();
            this.image = propBugRep.getImage();
            this.imageNumber = propBugRep.getImageNumber();
            this.checkedAt = propBugRep.getCheckedAt();
            return this;
        }

        public ProposalOrBugReportEntity build() {
            return new ProposalOrBugReportEntity(this.ulid, this.member, this.title, this.content, this.image, this.imageNumber, this.checkedAt);
        }
    }
}
