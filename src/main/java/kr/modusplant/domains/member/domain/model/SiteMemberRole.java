<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/domain/model/SiteMemberRole.java
package kr.modusplant.domains.member.domain.model;
========
package kr.modusplant.api.crud.member.domain.model;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/main/java/kr/modusplant/api/crud/member/domain/model/SiteMemberRole.java

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

    public static class SiteMemberRoleBuilder {
        private UUID uuid;
        private Role role;

        public SiteMemberRoleBuilder memberRole(SiteMemberRole memberRole) {
            this.uuid = memberRole.getUuid();
            this.role = memberRole.getRole();
            return this;
        }

        public SiteMemberRole build() {
            return new SiteMemberRole(this.uuid, this.role);
        }
    }
}
