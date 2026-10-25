package kr.modusplant.domains.comment.framework.outbound.jpa.repository;

import kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode;
import kr.modusplant.domains.comment.domain.vo.CommentAuthorId;
import kr.modusplant.domains.comment.usecase.port.repository.CommentAuthorRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.exception.NotFoundValueException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentAuthorRepositoryJpaAdapter implements CommentAuthorRepository {
    private final MemberJpaRepository memberRepository;

    @Override
    public Role getRole(CommentAuthorId commentAuthorId) {
        return memberRepository.findByUuid(commentAuthorId.getValue())
                .map(MemberEntity::getRole)
                .orElseThrow(() -> new NotFoundValueException(CommentErrorCode.NOT_FOUND_COMMENT_AUTHOR, "commentAuthorId"));
    }

    @Override
    public boolean isExists(CommentAuthorId commentAuthorId) {
        return memberRepository.existsById(commentAuthorId.getValue());
    }
}
