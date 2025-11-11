package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.domain.exception.EmptyCategoryIdException;
import kr.modusplant.domains.post.domain.exception.InvalidCategoryIdException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

import static kr.modusplant.shared.constant.Regex.PATTERN_UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrimaryCategoryId {

    private final UUID value;

    public static PrimaryCategoryId fromUuid(UUID uuid) {
        if (uuid == null) {
            throw new EmptyCategoryIdException();
        }
        return new PrimaryCategoryId(uuid);
    }

    public static PrimaryCategoryId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new EmptyCategoryIdException();
        }
        if (!PATTERN_UUID.matcher(value).matches()) {
            throw new InvalidCategoryIdException();
        }
        return new PrimaryCategoryId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PrimaryCategoryId primaryCategoryId)) return false;

        return new EqualsBuilder().append(getValue(), primaryCategoryId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
