package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.domain.exception.EmptyCategoryIdException;
import kr.modusplant.domains.post.domain.exception.InvalidCategoryIdException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

import static kr.modusplant.shared.constant.IdPattern.UUID_PATTERN;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SecondaryCategoryId {
    private final UUID value;

    public static SecondaryCategoryId fromUuid(UUID uuid) {
        if (uuid == null) {
            throw new EmptyCategoryIdException();
        }
        return new SecondaryCategoryId(uuid);
    }

    public static SecondaryCategoryId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new EmptyCategoryIdException();
        }
        if (!UUID_PATTERN.matcher(value).matches()) {
            throw new InvalidCategoryIdException();
        }
        return new SecondaryCategoryId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SecondaryCategoryId secondaryCategoryId)) return false;

        return new EqualsBuilder().append(getValue(), secondaryCategoryId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
