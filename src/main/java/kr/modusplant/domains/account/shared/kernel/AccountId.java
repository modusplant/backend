package kr.modusplant.domains.account.shared.kernel;

import kr.modusplant.domains.account.shared.exception.EmptyAccountIdException;
import kr.modusplant.domains.account.shared.exception.InvalidAccountIdException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

import static kr.modusplant.shared.constant.Regex.PATTERN_UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountId {
    private final UUID value;

    public static AccountId create(UUID uuid) {
        return AccountId.fromUuid(uuid);
    }

    public static AccountId fromUuid(UUID uuid) {
        if (uuid == null) {
            throw new EmptyAccountIdException();
        }
        return new AccountId(uuid);
    }

    public static AccountId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new EmptyAccountIdException();
        } else if (!PATTERN_UUID.matcher(value).matches()) {
            throw new InvalidAccountIdException();
        }
        return new AccountId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AccountId accountId)) return false;

        return new EqualsBuilder().append(getValue(), accountId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
