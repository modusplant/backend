package kr.modusplant.domains.comment.adapter.mapper;

import kr.modusplant.domains.comment.support.utils.adapter.CommentRegisterRequestTestUtils;
import kr.modusplant.domains.comment.support.utils.domain.CommentTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CommentMapperImplTest implements
        CommentRegisterRequestTestUtils, CommentTestUtils {

    private final CommentMapperImpl mapper = new CommentMapperImpl();

    @Test
    @DisplayName("CommentRegisterRequest 를 Comment 로 변환")
    public void callToComment_whenValidRegisterRequest_willReturnComment() {
        assertThat(mapper.toComment(testCommentRegisterRequest)).isEqualTo(testValidComment);
    }
}
