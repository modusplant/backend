package kr.modusplant.domains.member.app.http.response;

import java.util.UUID;

public record SiteMemberTermResponse(UUID uuid, String agreedTermsOfUseVersion, String agreedPrivacyPolicyVersion, String agreedAdInfoReceivingVersion) {
}
