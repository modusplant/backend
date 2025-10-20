package kr.modusplant.domains.term.domain.aggregate;

import kr.modusplant.domains.term.domain.exception.EmptyTermContentException;
import kr.modusplant.domains.term.domain.exception.EmptyTermIdException;
import kr.modusplant.domains.term.domain.exception.EmptyTermNameException;
import kr.modusplant.domains.term.domain.exception.EmptyTermVersionException;
import kr.modusplant.domains.term.domain.vo.TermContent;
import kr.modusplant.domains.term.domain.vo.TermId;
import kr.modusplant.domains.term.domain.vo.TermName;
import kr.modusplant.domains.term.domain.vo.TermVersion;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Term {
    private final TermId termId;
    private final TermName termName;
    private final TermContent termContent;
    private final TermVersion termVersion;

    public static Term create(TermName name, TermContent content, TermVersion version) {
        if(name == null) throw new EmptyTermNameException();
        if(content == null) throw new EmptyTermContentException();
        if(version == null) throw new EmptyTermVersionException();

        return new Term(null, name, content, version);
    }

    public static Term create(TermId termId, TermName termName, TermContent termContent, TermVersion termVersion) {
        return new Term(termId, termName, termContent, termVersion);
    }

    public Term create(TermContent content) {
        if(content == null) throw new EmptyTermContentException();

        return new Term(this.termId, this.termName, content, this.termVersion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Term term)) return false;

        return new EqualsBuilder().append(getTermId(), term.getTermId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getTermId()).toHashCode();
    }
}
