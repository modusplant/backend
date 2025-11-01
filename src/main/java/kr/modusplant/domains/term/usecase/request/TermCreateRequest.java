package kr.modusplant.domains.term.usecase.request;

import jakarta.validation.constraints.NotBlank;

public record TermCreateRequest(
        @NotBlank(message = "약관명이 비어 있습니다. ")
        String termName,
        @NotBlank(message = "약관버전이 비어 있습니다. ")
        String termVersion,
        @NotBlank(message = "약관내용이 비어 있습니다. ")
        String termContent
        ) {
}
