package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_PROPOSAL_OR_BUG_REPORT_IMAGE_NUMBER;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.PROPOSAL_OR_BUG_REPORT_IMAGE_NUMBER_OUT_OF_RANGE;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ProposalOrBugReportImageNumber {
    private final int value;

    @Getter
    private final boolean empty;

    private static final ProposalOrBugReportImageNumber emptyProposalOrBugReportImageNumber =
            new ProposalOrBugReportImageNumber(-1, true);

    public static ProposalOrBugReportImageNumber create(Integer value) {
        if (value == null) {
            return emptyProposalOrBugReportImageNumber;
        }
        if (value < 0 || value > 3) {
            throw new InvalidValueException(PROPOSAL_OR_BUG_REPORT_IMAGE_NUMBER_OUT_OF_RANGE, "proposalOrBugReportImageNumber");
        }
        return new ProposalOrBugReportImageNumber(value, false);
    }

    public static ProposalOrBugReportImageNumber createEmpty() {
        return emptyProposalOrBugReportImageNumber;
    }

    public int getValueIfNotEmpty() {
        if (isEmpty()) {
            throw new InvalidValueException(NOT_FOUND_PROPOSAL_OR_BUG_REPORT_IMAGE_NUMBER, "proposalOrBugReportImageNumber");
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ProposalOrBugReportImageNumber proposalOrBugReportImageNumber)) return false;

        return new EqualsBuilder().append(getValueIfNotEmpty(), proposalOrBugReportImageNumber.getValueIfNotEmpty()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValueIfNotEmpty()).toHashCode();
    }
}
