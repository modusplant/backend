package kr.modusplant.domains.communication.qna.common.util.app.http.response;

import kr.modusplant.domains.communication.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.communication.qna.common.util.domain.QnaPostTestUtils;

import java.time.LocalDateTime;

public interface QnaPostResponseTestUtils extends QnaPostTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2025, 6, 1, 0, 0);

    QnaPostResponse testQnaPostResponse = new QnaPostResponse(
            qnaPostWithUlid.getUlid(),
            testQnaCategoryWithUuid.getCategory(),
            qnaPostWithUlid.getCategoryUuid(),
            testQnaCategoryWithUuid.getOrder(),
            qnaPostWithUlid.getAuthMemberUuid(),
            memberBasicUserWithUuid.getNickname(),
            5,
            76L,
            qnaPostWithUlid.getTitle(),
            qnaPostWithUlid.getContent(),
            testDate,
            testDate.plusMinutes(24)
    );
}
