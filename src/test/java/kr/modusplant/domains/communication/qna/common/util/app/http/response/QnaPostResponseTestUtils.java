package kr.modusplant.domains.communication.qna.common.util.app.http.response;

import kr.modusplant.domains.communication.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.communication.qna.common.util.domain.QnaPostTestUtils;

import java.time.LocalDateTime;

public interface QnaPostResponseTestUtils extends QnaPostTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2025, 6, 1, 0, 0);

    QnaPostResponse testQnaPostResponse = new QnaPostResponse(
            testQnaPostWithUlid.getUlid(),
            testQnaCategoryWithUuid.getCategory(),
            testQnaPostWithUlid.getCategoryUuid(),
            testQnaCategoryWithUuid.getOrder(),
            testQnaPostWithUlid.getAuthMemberUuid(),
            memberBasicUserWithUuid.getNickname(),
            5,
            76L,
            testQnaPostWithUlid.getTitle(),
            testQnaPostWithUlid.getContent(),
            testDate,
            testDate.plusMinutes(24)
    );
}
