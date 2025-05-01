package kr.modusplant.domains.term.app.http.request;

public record TermInsertRequest(String name, String content, String version) {
}