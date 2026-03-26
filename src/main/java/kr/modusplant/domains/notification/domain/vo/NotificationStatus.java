package kr.modusplant.domains.notification.domain.vo;

import kr.modusplant.shared.enums.NotificationStatusType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationStatus {
    private final NotificationStatusType status;

    public static NotificationStatus read() {
        return new NotificationStatus(NotificationStatusType.READ);
    }

    public static NotificationStatus unread() {
        return new NotificationStatus(NotificationStatusType.UNREAD);
    }

    public boolean isRead() {
        return this.status == NotificationStatusType.READ;
    }

    public boolean isUnread() {
        return this.status == NotificationStatusType.UNREAD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof NotificationStatus notificationStatus)) return false;

        return new EqualsBuilder().append(getStatus(), notificationStatus.getStatus()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getStatus()).toHashCode();
    }
}
