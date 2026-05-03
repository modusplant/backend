package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_MEMBER_STATUS;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberStatus {
    private final Status status;
    private static final MemberStatus memberStatusActive = new MemberStatus(Status.ACTIVE);
    private static final MemberStatus memberStatusInactive = new MemberStatus(Status.INACTIVE);

    public static MemberStatus active() {
        return memberStatusActive;
    }

    public static MemberStatus inactive() {
        return memberStatusInactive;
    }

    public static MemberStatus fromBoolean(Boolean isActive) {
        if (isActive == null) {
            throw new EmptyValueException(EMPTY_MEMBER_STATUS, "memberStatus");
        }
        if (isActive.equals(true)) {
            return memberStatusActive;
        } else {
            return memberStatusInactive;
        }
    }

    public boolean isActive() {
        return getStatus() == Status.ACTIVE;
    }

    public boolean isInactive() {
        return getStatus() == Status.INACTIVE;
    }

    public String getValue() {
        return getStatus().getValue();
    }

    @Getter
    private enum Status {
        ACTIVE("active"),
        INACTIVE("inactive");

        private final String value;

        Status(String value) {
            this.value = value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberStatus that)) return false;

        return new EqualsBuilder().append(getStatus(), that.getStatus()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getStatus()).toHashCode();
    }
}