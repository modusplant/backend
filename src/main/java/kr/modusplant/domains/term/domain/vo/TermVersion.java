package kr.modusplant.domains.term.domain.vo;

import kr.modusplant.domains.term.domain.exception.EmptyTermVersionException;
import kr.modusplant.domains.term.domain.exception.InvalidTermVersionException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TermVersion implements Comparable<TermVersion> {
    private final String value;

    private static final Pattern VERSION_PATTERN = Pattern.compile("^v(\\d+)\\.(\\d+)\\.(\\d+)$");

    private final int major;
    private final int minor;
    private final int patch;

    public static TermVersion create(String value) {
        if (value == null || value.isBlank()) {
            throw new EmptyTermVersionException();
        }

        Matcher matcher = VERSION_PATTERN.matcher(value.trim());
        if (!matcher.matches()) {
            throw new InvalidTermVersionException();
        }
        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = Integer.parseInt(matcher.group(3));

        return new TermVersion(value.trim(), major, minor, patch);
    }

    @Override
    public int compareTo(TermVersion other) {
        if (major != other.major) return Integer.compare(major, other.major);
        if (minor != other.minor) return Integer.compare(minor, other.minor);
        return Integer.compare(patch, other.patch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TermVersion termVersion)) return false;
        return new EqualsBuilder()
                .append(this.major, termVersion.major)
                .append(this.minor, termVersion.minor)
                .append(this.patch, termVersion.patch)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(major)
                .append(minor)
                .append(patch)
                .toHashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
