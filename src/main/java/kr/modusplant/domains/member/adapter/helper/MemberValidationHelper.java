package kr.modusplant.domains.member.adapter.helper;

import kr.modusplant.domains.member.domain.vo.ActivitySubjectCommentId;
import kr.modusplant.domains.member.domain.vo.ActivitySubjectPostId;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.ReportId;
import kr.modusplant.domains.member.usecase.port.repository.*;
import kr.modusplant.shared.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;

@Component
@RequiredArgsConstructor
public class MemberValidationHelper {
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final ReportRepository reportRepository;
    private final ActivitySubjectPostRepository activitySubjectPostRepository;
    private final ActivitySubjectCommentRepository activitySubjectCommentRepository;

    public void validateIfMemberExists(MemberId memberId) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER_ID, "memberId");
        }
    }

    public void validateIfMemberProfileExists(MemberId memberId) {
        if (!memberProfileRepository.isIdExist(memberId)) {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER_PROFILE, "memberProfile");
        }
    }

    public void validateIfNicknameNotDuplicated(Nickname nickname) {
        if (memberRepository.isNicknameExist(nickname)) {
            throw new ExistsEntityException(KernelErrorCode.EXISTS_NICKNAME, "nickname");
        }
    }

    public void validateIfActivitySubjectPostExists(ActivitySubjectPostId activitySubjectPostId) {
        if (!activitySubjectPostRepository.isIdExist(activitySubjectPostId)) {
            throw new NotFoundEntityException(NOT_FOUND_ACTIVITY_SUBJECT_POST_ID, "activitySubjectPostId");
        }
    }

    public void validateIfActivitySubjectCommentExists(ActivitySubjectCommentId activitySubjectCommentId) {
        if (!activitySubjectCommentRepository.isIdExist(activitySubjectCommentId)) {
            throw new NotFoundEntityException(NOT_FOUND_ACTIVITY_SUBJECT_COMMENT_ID, "activitySubjectCommentId");
        }
    }

    public void validateIfReportExists(ReportId reportId) {
        if (!reportRepository.isIdExistInProposalOrBugReport(reportId)) {
            throw new NotFoundEntityException(NOT_FOUND_REPORT_ID, "reportId");
        }
    }
}
