package kr.modusplant.domains.account.social.framework.outbound.client.dto;

public record IdTokenInfo(
        String id,
        String email,
        String nickname
) {
}
