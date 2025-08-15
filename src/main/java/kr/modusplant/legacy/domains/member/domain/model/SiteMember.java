package kr.modusplant.legacy.domains.member.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder
public class SiteMember {
    private final UUID uuid;

    private final String nickname;

    private final LocalDate birthDate;

    private final Boolean isActive;

    private final Boolean isDisabledByLinking;

    private final Boolean isBanned;

    private final Boolean isDeleted;

    private final LocalDateTime loggedInAt;

    public static class SiteMemberBuilder {
        private UUID uuid;
        private String nickname;
        private LocalDate birthDate;
        private Boolean isActive;
        private Boolean isDisabledByLinking;
        private Boolean isBanned;
        private Boolean isDeleted;
        private LocalDateTime loggedInAt;

        public SiteMemberBuilder member(SiteMember member) {
            this.uuid = member.getUuid();
            this.nickname = member.getNickname();
            this.birthDate = member.getBirthDate();
            this.isActive = member.getIsActive();
            this.isDisabledByLinking = member.getIsDisabledByLinking();
            this.isBanned = member.getIsBanned();
            this.isDeleted = member.getIsDeleted();
            this.loggedInAt = member.getLoggedInAt();
            return this;
        }

        public SiteMember build() {
            return new SiteMember(this.uuid, this.nickname, this.birthDate, this.isActive, this.isDisabledByLinking, this.isBanned, this.isDeleted, this.loggedInAt);
        }
    }
}