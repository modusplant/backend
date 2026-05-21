package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;

import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface ActivitySubjectPostIdTestUtils {
    ActivitySubjectPostId testActivitySubjectPostId = ActivitySubjectPostId.create(TEST_POST_ULID);
}
