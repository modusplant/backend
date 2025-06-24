package kr.modusplant.domains.communication.tip.common.util.app.http.response;

import kr.modusplant.domains.communication.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.communication.tip.common.util.domain.TipPostTestUtils;

import java.time.LocalDateTime;

public interface TipPostResponseTestUtils extends TipPostTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2025, 6, 1, 0, 0);

    TipPostResponse testTipPostResponse = new TipPostResponse(
            testTipPostWithUlid.getUlid(),
            testTipCategoryWithUuid.getCategory(),
            testTipPostWithUlid.getCategoryUuid(),
            testTipCategoryWithUuid.getOrder(),
            testTipPostWithUlid.getAuthMemberUuid(),
            memberBasicUserWithUuid.getNickname(),
            5,
            76L,
            testTipPostWithUlid.getTitle(),
            testTipPostWithUlid.getContent(),
            testDate,
            testDate.plusMinutes(24)
    );
}
