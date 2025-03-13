package kr.modusplant.global.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class SiteMember {
    private final UUID uuid;

    private final String nickname;

    private final LocalDate birthDate;

    private final Boolean isActive;

    private final Boolean isDisabledByLinking;

    private final Boolean isBanned;

    private final Boolean isDeleted;

    private final LocalDateTime loggedInAt;
}