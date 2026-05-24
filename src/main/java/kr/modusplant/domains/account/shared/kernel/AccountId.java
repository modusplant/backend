package kr.modusplant.domains.account.shared.kernel;

import kr.modusplant.domains.account.shared.exception.enums.AccountErrorCode;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
            throw new EmptyValueException(AccountErrorCode.EMPTY_ACCOUNT_ID, "accountId");
        }
        return new AccountId(uuid);
    }

    public static AccountId fromString(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(AccountErrorCode.EMPTY_ACCOUNT_ID, "accountId");
        } else if (!PATTERN_UUID.matcher(value).matches()) {
            throw new InvalidValueException(AccountErrorCode.INVALID_ACCOUNT_ID, "accountId");
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
