package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyMemberStatusException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberStatus {
    private final Status status;

    public static MemberStatus active() {
        return new MemberStatus(Status.ACTIVE);
    }

    public static MemberStatus inactive() {
        return new MemberStatus(Status.INACTIVE);
    }

    public static MemberStatus fromBoolean(Boolean bool) {
        if (bool == null) {
            throw new EmptyMemberStatusException();
        } else if (bool.equals(true)) {
            return MemberStatus.active();
        } else {
            return MemberStatus.inactive();
        }
    }

    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public boolean isInactive() {
        return status == Status.INACTIVE;
    }

    @Getter
    public enum Status {
        ACTIVE("활동 중"),
        INACTIVE("활동 정지");

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