package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.domain.exception.EmptyAuthorIdException;
import kr.modusplant.domains.post.domain.exception.InvalidAuthorIdException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

import static kr.modusplant.shared.constant.IdPattern.UUID_PATTERN;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorId {
    private final UUID value;

    public static AuthorId fromUuid(UUID uuid) {
        if (uuid == null) {
            throw new EmptyAuthorIdException();
        }
        return new AuthorId(uuid);
    }

    public static AuthorId fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new EmptyAuthorIdException();
        }
        if (!UUID_PATTERN.matcher(value).matches()) {
            throw new InvalidAuthorIdException();
        }
        return new AuthorId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AuthorId authorId)) return false;

        return new EqualsBuilder().append(getValue(), authorId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
