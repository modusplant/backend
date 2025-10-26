package kr.modusplant.domains.term.usecase.response;

import java.util.UUID;

public record SiteMemberTermResponse(UUID uuid, String agreedTermsOfUseVersion, String agreedPrivacyPolicyVersion, String agreedAdInfoReceivingVersion) {
}
