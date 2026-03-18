package kr.modusplant.domains.member.adapter.helper;

import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.domain.vo.TargetPostId;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetCommentIdRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetPostIdRepository;
import kr.modusplant.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;

@Component
@RequiredArgsConstructor
public class MemberValidationHelper {
    private final MemberRepository memberRepository;
    private final TargetPostIdRepository targetPostIdRepository;
    private final TargetCommentIdRepository targetCommentIdRepository;

    public void validateIfMemberExists(MemberId memberId) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER_ID, "memberId");
        }
    }

    public void validateIfNicknameNotDuplicated(Nickname nickname) {
        if (memberRepository.isNicknameExist(nickname)) {
            throw new ExistsEntityException(KernelErrorCode.EXISTS_NICKNAME, "nickname");
        }
    }

    public void validateIfTargetPostExists(TargetPostId targetPostId) {
        if (!targetPostIdRepository.isIdExist(targetPostId)) {
            throw new NotFoundEntityException(NOT_FOUND_TARGET_POST_ID, "targetPostId");
        }
    }

    public void validateIfTargetCommentExists(TargetCommentId targetCommentId) {
        if (!targetCommentIdRepository.isIdExist(targetCommentId)) {
            throw new NotFoundEntityException(NOT_FOUND_TARGET_COMMENT_ID, "targetCommentId");
        }
    }
}
