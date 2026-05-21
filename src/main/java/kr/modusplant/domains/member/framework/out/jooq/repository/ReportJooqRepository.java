package kr.modusplant.domains.member.framework.out.jooq.repository;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.modusplant.jooq.Tables.PROP_BUG_REP;
import static org.jooq.impl.DSL.field;

@Repository
@RequiredArgsConstructor
public class ReportJooqRepository {
    private final DSLContext dsl;

    public @NotNull List<String> getImageFileKeysFromReportId(String reportId) {
        return dsl.select(
                        field("jsonb_array_elements({0}) ->> 'src'", String.class, PROP_BUG_REP.IMAGE)
                )
                .from(PROP_BUG_REP)
                .where(PROP_BUG_REP.ULID.eq(reportId))
                .fetchInto(String.class);
    }
}
