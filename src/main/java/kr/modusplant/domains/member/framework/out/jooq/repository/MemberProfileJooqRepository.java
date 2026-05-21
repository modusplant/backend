package kr.modusplant.domains.member.framework.out.jooq.repository;

import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_MEMBER_PROFILE;
import static kr.modusplant.jooq.Tables.SITE_MEMBER_PROF;

@Repository
@RequiredArgsConstructor
public class MemberProfileJooqRepository {
    private final DSLContext dsl;

    public String getImageFileKeyFromMemberId(UUID memberId) {
        return dsl.select(SITE_MEMBER_PROF.IMAGE_PATH)
                .from(SITE_MEMBER_PROF)
                .where(SITE_MEMBER_PROF.UUID.eq(memberId))
                .fetchOptional()
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_MEMBER_PROFILE, "memberProfile"))
                .value1();
    }
}
