package kr.modusplant.domains.member.app.http.request;

import java.util.UUID;

public record SiteMemberTermInsertRequest(UUID uuid, String agreedTermsOfUseVersion, String agreedPrivacyPolicyVersion, String agreedAdInfoReceivingVersion) {
}
