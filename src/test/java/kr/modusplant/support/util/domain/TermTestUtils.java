package kr.modusplant.support.util.domain;

import kr.modusplant.global.domain.model.Term;

import java.util.UUID;

import static kr.modusplant.global.util.VersionUtils.createVersion;

public interface TermTestUtils {
    Term termsOfUse = Term.builder()
            .name("이용약관")
            .content("이용약관 내용")
            .version(createVersion(1, 0, 0))
            .build();

    Term termsOfUseWithUuid = Term.builder()
            .uuid(UUID.fromString("815e03c6-04db-4c6a-b76f-7b6320f17f38"))
            .name(termsOfUse.getName())
            .content(termsOfUse.getContent())
            .version(termsOfUse.getVersion())
            .build();

    Term privacyPolicy = Term.builder()
            .name("개인정보처리방침")
            .content("개인정보처리방침 내용")
            .version(createVersion(1, 0, 2))
            .build();

    Term privacyPolicyWithUuid = Term.builder()
            .uuid(UUID.fromString("275fd6ad-6c2a-4c70-99fd-3e2f6744dfa8"))
            .name(privacyPolicy.getName())
            .content(privacyPolicy.getContent())
            .version(privacyPolicy.getVersion())
            .build();

    Term adInfoReceiving = Term.builder()
            .name("광고성 정보 수신")
            .content("광고성 정보 수신 내용")
            .version(createVersion(1, 0, 4))
            .build();

    Term adInfoReceivingWithUuid = Term.builder()
            .uuid(UUID.fromString("b45d3cc6-7c4a-42eb-b48b-07b47ea41fac"))
            .name(adInfoReceiving.getName())
            .content(adInfoReceiving.getContent())
            .version(adInfoReceiving.getVersion())
            .build();
}
