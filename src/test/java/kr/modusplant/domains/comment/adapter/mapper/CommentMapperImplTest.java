package kr.modusplant.domains.comment.adapter.mapper;

import kr.modusplant.domains.comment.common.util.adapter.CommentRegisterRequestTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentTestUtils;

public class CommentMapperImplTest implements
        CommentRegisterRequestTestUtils, CommentTestUtils {

    private final CommentMapperImpl mapper = new CommentMapperImpl();

//    @Test
//    @DisplayName("댓글 등록 객체를 댓글 객체로 변환")
//    public void testToComment_givenValidRegisterRequest_willReturnComment() {
//        assertThat(mapper.toComment(testCommentRegisterRequest)).isEqualTo(testValidComment);
//    }
}
