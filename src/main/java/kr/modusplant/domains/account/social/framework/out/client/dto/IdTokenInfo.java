package kr.modusplant.domains.account.social.framework.out.client.dto;

public record IdTokenInfo(
        String id,
        String email,
        String nickname
) {
}
