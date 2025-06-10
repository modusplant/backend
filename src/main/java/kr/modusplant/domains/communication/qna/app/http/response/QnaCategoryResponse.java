package kr.modusplant.domains.communication.qna.app.http.response;

import java.util.UUID;

public record QnaCategoryResponse(UUID uuid, String category, Integer order) {
}
