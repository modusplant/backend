package kr.modusplant.legacy.domains.member.app.http.request;

import java.util.UUID;

public record SiteMemberTermUpdateRequest(UUID uuid, String agreedTermsOfUseVersion, String agreedPrivacyPolicyVersion, String agreedAdInfoReceivingVersion) {
}
