package kr.modusplant.domains.comment.adapter.mapper;

import kr.modusplant.domains.comment.common.util.adapter.CommentRegisterRequestTestUtils;
import kr.modusplant.domains.comment.common.util.domain.CommentTestUtils;
import kr.modusplant.framework.aws.service.S3FileService;
import org.mockito.Mockito;

public class CommentMapperImplTest implements
        CommentRegisterRequestTestUtils, CommentTestUtils {

    private final S3FileService fileService = Mockito.mock(S3FileService.class);
    private final CommentMapperImpl mapper = new CommentMapperImpl(fileService);

//    @Test
//    @DisplayName("댓글 등록 객체를 댓글 객체로 변환")
//    public void testToComment_givenValidRegisterRequest_willReturnComment() {
//        assertThat(mapper.toComment(testCommentRegisterRequest)).isEqualTo(testValidComment);
//    }
}
