package kr.modusplant.domains.comment.adapter.controller;

import kr.modusplant.domains.comment.adapter.mapper.CommentMapperImpl;
import kr.modusplant.domains.comment.common.util.adapter.CommentReadModelTestUtils;
import kr.modusplant.domains.comment.common.util.adapter.CommentResponseTestUtils;
import kr.modusplant.domains.comment.common.util.adapter.MemberReadModelTestUtils;
import kr.modusplant.domains.comment.common.util.domain.AuthorTestUtils;
import kr.modusplant.domains.comment.common.util.domain.PostIdTestUtils;
import kr.modusplant.domains.comment.framework.in.web.cache.CommentCacheService;
import kr.modusplant.domains.comment.framework.out.persistence.jooq.CommentJooqRepository;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.CommentRepositoryJpaAdapter;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.swear.service.SwearService;
import org.mockito.Mockito;

public class CommentControllerTest implements PostIdTestUtils, AuthorTestUtils,
        CommentReadModelTestUtils, MemberReadModelTestUtils, CommentResponseTestUtils {
    private final CommentMapperImpl mapper = Mockito.mock(CommentMapperImpl.class);
    private final CommentJooqRepository jooqAdapter = Mockito.mock(CommentJooqRepository.class);
    private final CommentRepositoryJpaAdapter jpaAdapter = Mockito.mock(CommentRepositoryJpaAdapter.class);
    private final CommPostJpaRepository postJpaRepository = Mockito.mock(CommPostJpaRepository.class);
    private final SiteMemberJpaRepository memberJpaRepository = Mockito.mock(SiteMemberJpaRepository.class);
    private final SwearService swearService = Mockito.mock(SwearService.class);
    private final CommentCacheService cacheService = Mockito.mock(CommentCacheService.class);
    private final CommentController controller = new CommentController(mapper, jooqAdapter,
            jpaAdapter, postJpaRepository, memberJpaRepository, swearService, cacheService);

//    @Test
//    @DisplayName("유효한 게시글 id로 댓글 읽기")
//    public void testGatherByPost_givenValidPostUlid_willReturnResponseList() {
//        // given
//        given(jooqAdapter.findByPost(testPostId)).willReturn(List.of(testCommentResponse));
//
//        // when
//        List<CommentResponse> result = controller.gatherByPost(testPostId.getId());
//
//        // then
//        assertThat(result).isEqualTo(List.of(testCommentResponse));
//    }

//    @Test
//    @DisplayName("유효한 작성자 id로 댓글 읽기")
//    public void testGatherByAuthor_givenValidPostUlid_willReturnResponseList() {
//        // given
//        given(jooqAdapter.findByAuthor(testAuthorWithUuid)).willReturn(List.of(testCommentResponse));
//
//        // when
//        List<CommentResponse> result = controller.gatherByAuthor(testAuthorWithUuid.getMemberUuid());
//
//        // then
//        assertThat(result).isEqualTo(List.of(testCommentResponse));
//    }
}
