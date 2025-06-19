package kr.modusplant.domains.communication.tip.common.util.app.http.response;

import kr.modusplant.domains.communication.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.communication.tip.common.util.domain.TipPostTestUtils;

import java.time.LocalDateTime;

public interface TipPostResponseTestUtils extends TipPostTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2025, 6, 1, 0, 0);

    TipPostResponse testTipPostResponse = new TipPostResponse(
            tipPostWithUlid.getUlid(),
            testTipCategoryWithUuid.getCategory(),
            tipPostWithUlid.getCategoryUuid(),
            testTipCategoryWithUuid.getOrder(),
            tipPostWithUlid.getAuthMemberUuid(),
            memberBasicUserWithUuid.getNickname(),
            5,
            76L,
            tipPostWithUlid.getTitle(),
            tipPostWithUlid.getContent(),
            testDate,
            testDate.plusMinutes(24)
    );
}
