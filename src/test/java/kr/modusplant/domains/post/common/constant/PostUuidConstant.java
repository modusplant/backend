package kr.modusplant.domains.post.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostUuidConstant {
    public static final UUID TEST_POST_UUID = UUID.fromString("7a071932-b666-4c37-925e-ede1593ba9b8");
    public static final UUID TEST_POST_UUID2 = UUID.fromString("9a067122-b666-4c37-925e-ede1593ba9b9");
}
