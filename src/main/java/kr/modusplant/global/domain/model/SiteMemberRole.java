package kr.modusplant.global.domain.model;

import kr.modusplant.global.enums.Role;
import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
public class SiteMemberRole {
    private final UUID uuid;

    private final Role role;
}
