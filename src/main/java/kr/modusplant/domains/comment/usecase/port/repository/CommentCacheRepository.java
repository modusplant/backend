package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;

import java.util.Optional;
import java.util.function.IntSupplier;

public interface CommentCacheRepository {

    /**
     * 동일 게시글의 같은 부모 아래에서 다음 형제 경로를 예약한다.
     *
     * @param getMaximumSiblingPathOrderFromDB Redis 형제 순서 카운터 키가 없을 때에만 호출되는 DB 하한값 조회.
     *                                  카운터가 살아 있는 활성 게시글에서는 호출되지 않아 불필요한 DB 조회를 막는다.
     * @return 신규 요청이면 예약된 CommentPath, 이미 처리된 (게시글, 댓글 경로, 작성자) 요청이면
     *         {@link Optional#empty()} (멱등 보장을 위한 무시 처리)
     */
    Optional<CommentPath> reservePath(PostId postId, CommentPath requestedPath, Author author,
                                      IntSupplier getMaximumSiblingPathOrderFromDB);

    /**
     * 댓글 경로가 포함된 모든 캐시(멱등성 마커, 형제 순서 카운터)를 만료시킨다.
     * 기존 댓글 경로가 일괄 변경되어 캐시된 경로 정보가 DB와 어긋날 때 사용한다.
     *
     * @return 만료된 캐시 키의 개수
     */
    long evictAllPathCaches();
}
