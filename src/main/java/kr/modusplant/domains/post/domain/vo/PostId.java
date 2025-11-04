package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.domain.exception.EmptyPostIdException;
import kr.modusplant.domains.post.domain.exception.InvalidPostIdException;
import kr.modusplant.infrastructure.persistence.generator.UlidIdGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.generator.EventType;

import static kr.modusplant.shared.constant.IdPattern.ULID_PATTERN;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostId {
    private static final UlidIdGenerator generator = new UlidIdGenerator();

    private final String value;

    public static PostId generate() {
        return new PostId(generator.generate(null, null, null, EventType.INSERT));
    }

    public static PostId create(String ulid) {
        if (ulid == null || ulid.trim().isEmpty()) {
            throw new EmptyPostIdException();
        }
        if (!isValidUlid(ulid)) {
            throw new InvalidPostIdException();
        }
        return new PostId(ulid);
    }

    private static boolean isValidUlid(String ulid) {
        if (StringUtils.isBlank(ulid) || ulid.length() != 26) {
            return false;
        }
        return ULID_PATTERN.matcher(ulid).matches();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PostId postId)) return false;

        return new EqualsBuilder().append(getValue(),postId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37).append(getValue()).toHashCode();
    }

}
