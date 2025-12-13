package kr.modusplant.domains.identity.account.domain.vo;

import kr.modusplant.domains.identity.account.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.shared.exception.enums.AccountErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountId {
    private final UUID value;

    public static AccountId create(UUID uuid) {
        if (uuid == null) {
            throw new EmptyValueException(AccountErrorCode.EMPTY_ACCOUNT_ID);
        }
        return new AccountId(uuid);
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
