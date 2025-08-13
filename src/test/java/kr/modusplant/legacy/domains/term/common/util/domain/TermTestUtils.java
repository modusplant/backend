package kr.modusplant.legacy.domains.term.common.util.domain;

import kr.modusplant.legacy.domains.term.domain.model.Term;

import java.util.UUID;

import static kr.modusplant.global.util.VersionUtils.createVersion;

public interface TermTestUtils {
    Term termsOfUse = Term.builder().name("이용약관").content("이용약관 내용").version(createVersion(1, 0, 0)).build();

    Term termsOfUseWithUuid = Term.builder().uuid(UUID.fromString("815e03c6-04db-4c6a-b76f-7b6320f17f38")).name(termsOfUse.getName()).content(termsOfUse.getContent()).version(termsOfUse.getVersion()).build();
}
