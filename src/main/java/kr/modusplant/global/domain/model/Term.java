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
}
