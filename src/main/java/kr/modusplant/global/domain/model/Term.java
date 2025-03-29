package kr.modusplant.global.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class Term {
    private final UUID uuid;

    private final String name;

    private final String content;

    private final String version;

    public static class TermBuilder {
        private UUID uuid;
        private String name;
        private String content;
        private String version;

        public TermBuilder term(Term term) {
            this.uuid = term.getUuid();
            this.name = term.getName();
            this.content = term.getContent();
            this.version = term.getVersion();
            return this;
        }

        public Term build() {
            return new Term(this.uuid, this.name, this.content, this.version);
        }
    }
}
