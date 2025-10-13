package kr.modusplant.domains.member.usecase.request;

public record TermCreateRequest(
        String termName,
        String termContent,
        String termVersion
) {
}
