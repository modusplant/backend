package kr.modusplant.domains.term.usecase.response;

import java.util.UUID;

public record TermResponse(UUID uuid, String name, String content, String version) {
}
