package kr.modusplant.domains.member.usecase.record;

public record MemberWithdrawalRecord(String authCode, String authProvider, String accessToken) {
}
