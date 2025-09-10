package kr.modusplant.domains.comment.framework.out.persistence.jpa.mapper;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.support.utils.domain.CommentTestUtils;
import kr.modusplant.domains.comment.support.utils.framework.CommentEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentJpaMapperTest implements CommentTestUtils, CommentEntityTestUtils {
    private final CommentJpaMapper mapper = new CommentJpaMapperImpl();

    @Test
    @DisplayName("")
    public void testToCommentEntity_givenValidComment_willReturnCommentEntity() {
        // given & when
        CommentEntity result = mapper.toCommentEntity(testValidComment);

        // then
        assertThat(result).isEqualTo(createCommentEntity());
    }

}
