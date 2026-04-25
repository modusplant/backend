package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberImageIOHelper;
import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.adapter.translator.MemberSocialTranslator;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.entity.nullobject.EmptyMemberProfileImage;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberProfileIntroduction;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyReportImagePath;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.domains.member.usecase.port.repository.*;
import kr.modusplant.domains.member.usecase.record.*;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import kr.modusplant.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.swear.exception.SwearContainedException;
import kr.modusplant.infrastructure.swear.service.SwearService;
import kr.modusplant.shared.event.*;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;

@SuppressWarnings("LoggingSimilarMessage")
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class MemberController {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final SwearService swearService;
    private final MemberImageIOHelper memberImageIOHelper;
    private final MemberValidationHelper memberValidationHelper;
    private final MemberProfileMapper memberProfileMapper;
    private final MemberSocialTranslator memberSocialTranslator;
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final TargetPostRepository targetPostRepository;
    private final TargetCommentRepository targetCommentRepository;
    private final ReportRepository reportRepository;
    private final EventBus eventBus;

    @Transactional(readOnly = true)
    public boolean checkExistedNickname(MemberNicknameCheckRecord record) {
        Nickname nickname = Nickname.create(record.nickname());
        return memberRepository.isNicknameExist(nickname);
    }

    @Transactional(readOnly = true)
    public MemberProfileResponse getProfile(MemberProfileGetRecord record) throws IOException {
        MemberId memberId = MemberId.fromUuid(record.id());
        memberValidationHelper.validateIfMemberExists(memberId);
        Optional<MemberProfile> optionalMemberProfile = memberProfileRepository.getById(memberId);
        if (optionalMemberProfile.isPresent()) {
            return memberProfileMapper.toMemberProfileResponse(optionalMemberProfile.orElseThrow());
        } else {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_PROFILE, "memberProfile");
        }
    }

    public MemberProfileResponse overrideProfile(MemberProfileOverrideRecord record) throws IOException {
        MemberId memberId = MemberId.fromUuid(record.id());
        Nickname memberNickname = Nickname.create(record.nickname());
        MemberProfile memberProfile;
        MemberProfileImage memberProfileImage;
        MemberProfileIntroduction memberProfileIntroduction;
        MultipartFile image = record.image();
        String introduction = record.introduction();
        validateBeforeOverrideProfile(memberId, memberNickname);

        Optional<MemberProfile> optionalMemberProfile = memberProfileRepository.getById(memberId);
        if (optionalMemberProfile.isPresent()) {
            memberImageIOHelper.deleteImage(optionalMemberProfile.orElseThrow().getMemberProfileImage());
        } else {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_PROFILE, "memberProfile");
        }
        if (!(image == null)) {
            String newImagePath = memberImageIOHelper.uploadImage(memberId, record);
            memberProfileImage = MemberProfileImage.create(
                    MemberProfileImagePath.create(newImagePath),
                    MemberProfileImageBytes.create(image.getBytes())
            );
        } else {
            memberProfileImage = EmptyMemberProfileImage.create();
        }
        if (!(introduction == null)) {
            introduction = swearService.filterSwear(introduction);
            memberProfileIntroduction = MemberProfileIntroduction.create(introduction);
        } else {
            memberProfileIntroduction = EmptyMemberProfileIntroduction.create();
        }
        memberProfile = MemberProfile.create(memberId, memberProfileImage, memberProfileIntroduction, memberNickname);
        return memberProfileMapper.toMemberProfileResponse(memberProfileRepository.update(memberProfile));
    }

    public void likePost(MemberPostLikeRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetPostExists(targetPostId);
        if (!targetPostRepository.isPublished(targetPostId)) {
            throw new NotAccessibleException(NOT_ACCESSIBLE_POST_LIKE, "postLike", targetPostId.getValue());
        }
        if (targetPostRepository.isUnliked(memberId, targetPostId)) {
            eventBus.publish(PostLikeEvent.create(memberId.getValue(), targetPostId.getValue()));
        }
    }

    public void unlikePost(MemberPostUnlikeRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetPostExists(targetPostId);
        if (!targetPostRepository.isPublished(targetPostId)) {
            throw new NotAccessibleException(NOT_ACCESSIBLE_POST_LIKE, "postUnlike", targetPostId.getValue());
        }
        if (targetPostRepository.isLiked(memberId, targetPostId)) {
            eventBus.publish(PostUnlikeEvent.create(memberId.getValue(), targetPostId.getValue()));
        }
    }

    public void bookmarkPost(MemberPostBookmarkRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetPostExists(targetPostId);
        if (!targetPostRepository.isPublished(targetPostId)) {
            throw new NotAccessibleException(NOT_ACCESSIBLE_POST_BOOKMARK, "postBookmark", targetPostId.getValue());
        }
        if (targetPostRepository.isNotBookmarked(memberId, targetPostId)) {
            eventBus.publish(PostBookmarkEvent.create(memberId.getValue(), targetPostId.getValue()));
        }
    }

    public void cancelPostBookmark(MemberPostBookmarkCancelRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetPostExists(targetPostId);
        if (!targetPostRepository.isPublished(targetPostId)) {
            throw new NotAccessibleException(NOT_ACCESSIBLE_POST_BOOKMARK, "postBookmark", targetPostId.getValue());
        }
        if (targetPostRepository.isBookmarked(memberId, targetPostId)) {
            eventBus.publish(PostBookmarkCancelEvent.create(memberId.getValue(), targetPostId.getValue()));
        }
    }

    public void likeComment(MemberCommentLikeRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetCommentId targetCommentId = TargetCommentId.create(
                TargetPostId.create(record.postUlid()), TargetCommentPath.create(record.path()));
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetCommentExists(targetCommentId);
        if (targetCommentRepository.isUnliked(memberId, targetCommentId)) {
            eventBus.publish(
                    CommentLikeEvent.create(
                            memberId.getValue(),
                            targetCommentId.getTargetPostId().getValue(),
                            targetCommentId.getTargetCommentPath().getValue()));
        }
    }

    public void unlikeComment(MemberCommentUnlikeRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetCommentId targetCommentId = TargetCommentId.create(
                TargetPostId.create(record.postUlid()), TargetCommentPath.create(record.path()));
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetCommentExists(targetCommentId);
        if (targetCommentRepository.isLiked(memberId, targetCommentId)) {
            eventBus.publish(
                    CommentUnlikeEvent.create(
                            memberId.getValue(),
                            targetCommentId.getTargetPostId().getValue(),
                            targetCommentId.getTargetCommentPath().getValue()));
        }
    }

    public void reportProposalOrBug(ProposalOrBugReportRecord record) throws IOException {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        ReportId reportId = ReportId.generate();
        ReportTitle reportTitle = ReportTitle.create(record.title());
        ReportContent reportContent = ReportContent.create(record.content());
        ReportImagePath reportImagePath;
        MultipartFile image = record.image();
        memberValidationHelper.validateIfMemberExists(memberId);
        if (!(image == null)) {
            reportImagePath = ReportImagePath.create(memberImageIOHelper.uploadImage(memberId, reportId, record));
        } else {
            reportImagePath = EmptyReportImagePath.create();
        }
        eventBus.publish(
                ProposalOrBugReportEvent.create(
                        memberId.getValue(),
                        reportId.getValue(),
                        reportTitle.getValue(),
                        reportContent.getValue(),
                        reportImagePath.getValue()));
    }

    public void removeProposalOrBug(ProposalOrBugReportRemoveRecord record) {
        ReportId reportId = ReportId.create(record.reportUlid());
        memberValidationHelper.validateIfReportExists(reportId);
        eventBus.publish(ProposalOrBugReportRemoveEvent.create(reportId.getValue()));
    }

    public void reportPostAbuse(PostAbuseReportRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetPostExists(targetPostId);
        if (!targetPostRepository.isPublished(targetPostId)) {
            throw new NotAccessibleException(
                    NOT_ACCESSIBLE_POST_REPORT_FOR_ABUSE, "postReportForAbuse", targetPostId.getValue());
        } else if (reportRepository.isMemberAbusePost(memberId, targetPostId)) {
            throw new ExistsEntityException(EntityErrorCode.EXISTS_POST_ABUSE_REPORT, "postAbuseReport");
        }
        eventBus.publish(PostAbuseReportEvent.create(memberId.getValue(), record.postUlid()));
    }

    public void reportCommentAbuse(CommentAbuseReportRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetCommentId targetCommentId = TargetCommentId.create(
                TargetPostId.create(record.postUlid()), TargetCommentPath.create(record.path()));
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetCommentExists(targetCommentId);
        if (reportRepository.isMemberAbuseComment(memberId, targetCommentId)) {
            throw new ExistsEntityException(EntityErrorCode.EXISTS_COMMENT_ABUSE_REPORT, "commentAbuseReport");
        }
        eventBus.publish(CommentAbuseReportEvent.create(memberId.getValue(), record.postUlid(), record.path()));
    }

    public void withdraw(MemberWithdrawalRecord record) {
        String accessToken = record.accessToken();
        String authCode = record.authCode();
        String authProvider = record.authProvider();
        MemberId memberId = MemberId.fromUuid(jwtTokenProvider.getMemberUuidFromToken(accessToken));
        memberValidationHelper.validateIfMemberExists(memberId);
        if (record.opinion().length() > 600) {
            throw new InvalidValueException(MemberErrorCode.MEMBER_WITHDRAW_OPINION_OVER_LENGTH, "opinion");
        }
        if (authCode != null && authProvider != null) {
            memberSocialTranslator.deleteSocialAccountWithSocialAccessToken(
                    memberSocialTranslator.getSocialAccessToken(authCode, authProvider),
                    authProvider,
                    memberId.getValue());
        } else if (authCode != null || authProvider != null) {
            throw new InvalidValueException(GeneralErrorCode.INVALID_INPUT, "authCode", "authProvider");
        }
        tokenService.blacklistAccessToken(accessToken);
        eventBus.publish(MemberWithdrawalEvent.create(memberId.getValue(), record.reason().name(), record.opinion()));
    }

    private void validateBeforeOverrideProfile(MemberId memberId, Nickname memberNickname) {
        memberValidationHelper.validateIfMemberExists(memberId);
        if (swearService.isSwearContained(memberNickname.getValue())) {
            throw new SwearContainedException();
        }
        Optional<Member> emptyOrMember = memberRepository.getByNickname(memberNickname);
        if (emptyOrMember.isPresent() && !emptyOrMember.orElseThrow().getMemberId().equals(memberId)) {
            throw new ExistsEntityException(KernelErrorCode.EXISTS_NICKNAME, "memberNickname");
        }
    }
}
