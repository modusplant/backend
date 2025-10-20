package kr.modusplant.legacy.domains.term.app.http.response;

import java.util.UUID;

public record TermResponse(UUID uuid, String name, String content, String version) {
}
