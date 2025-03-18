package kr.modusplant.support.util.domain;

import kr.modusplant.global.domain.model.Term;

import static kr.modusplant.global.util.VersionUtils.createVersion;

public interface TermTestUtils {
    Term termsOfUse = Term.builder()
            .name("이용약관")
            .content("이용약관 내용")
            .version(createVersion(1, 0, 0))
            .build();

    Term privacyPolicy = Term.builder()
            .name("개인정보처리방침")
            .content("개인정보처리방침 내용")
            .version(createVersion(1, 0, 2))
            .build();

    Term adInfoReceiving = Term.builder()
            .name("광고성 정보 수신")
            .content("광고성 정보 수신 내용")
            .version(createVersion(1, 0, 4))
            .build();
}
