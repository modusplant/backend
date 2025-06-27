package kr.modusplant.domains.communication.conversation.common.util.app.http.response;

import kr.modusplant.domains.communication.conversation.app.http.response.ConvPostResponse;
import kr.modusplant.domains.communication.conversation.common.util.domain.ConvPostTestUtils;

import java.time.LocalDateTime;

public interface ConvPostResponseTestUtils extends ConvPostTestUtils {
    LocalDateTime testDate = LocalDateTime.of(2025, 6, 1, 0, 0);

    ConvPostResponse testConvPostResponse = new ConvPostResponse(
            convPostWithUlid.getUlid(),
            testConvCategoryWithUuid.getCategory(),
            convPostWithUlid.getCategoryUuid(),
            testConvCategoryWithUuid.getOrder(),
            convPostWithUlid.getAuthMemberUuid(),
            memberBasicUserWithUuid.getNickname(),
            5,
            76L,
            convPostWithUlid.getTitle(),
            convPostWithUlid.getContent(),
            testDate,
            testDate.plusMinutes(24)
    );
}
