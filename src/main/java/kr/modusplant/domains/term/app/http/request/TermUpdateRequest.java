package kr.modusplant.domains.term.app.http.request;

import java.util.UUID;

public record TermUpdateRequest(UUID uuid, String content, String version) {
}