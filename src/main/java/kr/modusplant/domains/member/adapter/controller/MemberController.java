package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberImageIOHelper;
import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.adapter.translator.MemberSocialTranslator;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.aggregate.ProposalOrBugReport;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.entity.ProposalOrBugReportImage;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.domains.member.usecase.port.repository.*;
import kr.modusplant.domains.member.usecase.record.*;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.swear.exception.SwearContainedException;
import kr.modusplant.infrastructure.swear.service.SwearService;
import kr.modusplant.shared.event.CommentLikeNotificationEvent;
import kr.modusplant.shared.event.PostLikeNotificationEvent;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public boolean checkExistedNickname(MemberNicknameCheckRecord record) {
        Nickname nickname = Nickname.create(record.nickname());
        return memberRepository.isNicknameExist(nickname);
    }

    @Transactional(readOnly = true)
    public MemberProfileResponse getProfile(MemberProfileGetRecord record) throws IOException {
        MemberId memberId = MemberId.fromUuid(record.id());
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfMemberProfileExists(memberId);

        MemberProfile memberProfile = memberProfileRepository.getById(memberId);
        return memberProfileMapper.toMemberProfileResponse(memberProfile);
    }

    public MemberProfileResponse overrideProfile(MemberProfileOverrideRecord record) throws IOException {
        MemberId memberId = MemberId.fromUuid(record.id());
        Nickname memberNickname = Nickname.create(record.nickname());
        validateBeforeOverrideProfile(memberId, memberNickname);

        MemberProfile memberProfile = memberProfileRepository.getById(memberId);
        memberImageIOHelper.deleteImage(memberProfile.getMemberProfileImage());

        MultipartFile image = record.image();
        String newImagePath = null;
        byte[] newImageBytes = null;
        if (!(image == null)) {
            newImagePath = memberImageIOHelper.uploadImage(memberId, record);
            newImageBytes = image.getBytes();
        }
        MemberProfileImage memberProfileImage = MemberProfileImage.create(
                MemberProfileImagePath.create(newImagePath),
                MemberProfileImageBytes.create(newImageBytes)
        );

        String introduction = record.introduction();
        if (!(introduction == null)) {
            introduction = swearService.filterSwear(introduction);
        }
        MemberProfileIntroduction memberProfileIntroduction = MemberProfileIntroduction.create(introduction);
        memberProfile = MemberProfile.create(memberId, memberProfileImage, memberProfileIntroduction, memberNickname);
        return memberProfileMapper.toMemberProfileResponse(memberProfileRepository.update(memberProfile));
    }

    public void likePost(MemberPostLikeRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        validateBeforeLikePost(memberId, targetPostId);

        if (targetPostRepository.isUnliked(memberId, targetPostId)) {
            targetPostRepository.like(memberId, targetPostId);
            applicationEventPublisher.publishEvent(
                    PostLikeNotificationEvent.create(memberId.getValue(), targetPostId.getValue()));
        }
    }

    public void unlikePost(MemberPostUnlikeRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        validateBeforeUnlikePost(memberId, targetPostId);

        if (targetPostRepository.isLiked(memberId, targetPostId)) {
            targetPostRepository.unlike(memberId, targetPostId);
        }
    }

    public void bookmarkPost(MemberPostBookmarkRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        validateBeforeBookmarkOrCancelPostBookmark(memberId, targetPostId);

        if (targetPostRepository.isNotBookmarked(memberId, targetPostId)) {
            targetPostRepository.bookmark(memberId, targetPostId);
        }
    }

    public void cancelPostBookmark(MemberPostBookmarkCancelRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        validateBeforeBookmarkOrCancelPostBookmark(memberId, targetPostId);

        if (targetPostRepository.isBookmarked(memberId, targetPostId)) {
            targetPostRepository.cancelBookmark(memberId, targetPostId);
        }
    }

    public void likeComment(MemberCommentLikeRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetCommentId targetCommentId = TargetCommentId.create(
                TargetPostId.create(record.postUlid()), TargetCommentPath.create(record.path()));
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetCommentExists(targetCommentId);

        if (targetCommentRepository.isUnliked(memberId, targetCommentId)) {
            targetCommentRepository.like(memberId, targetCommentId);
            applicationEventPublisher.publishEvent(
                    CommentLikeNotificationEvent.create(
                            memberId.getValue(),
                            targetCommentId.getTargetPostId().getValue(),
                            targetCommentId.getTargetCommentPath().getValue()
                    )
            );
        }
    }

    public void unlikeComment(MemberCommentUnlikeRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetCommentId targetCommentId = TargetCommentId.create(
                TargetPostId.create(record.postUlid()), TargetCommentPath.create(record.path()));
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetCommentExists(targetCommentId);

        if (targetCommentRepository.isLiked(memberId, targetCommentId)) {
            targetCommentRepository.unlike(memberId, targetCommentId);
        }
    }

    public void reportProposalOrBug(ProposalOrBugReportRecord record) throws IOException {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        ReportId reportId = ReportId.generate();
        ReportTitle reportTitle = ReportTitle.create(record.title());
        ReportContent reportContent = ReportContent.create(record.content());
        List<MultipartFile> images = record.images();
        Integer imageNumber = record.imageNumber();
        validateBeforeReportProposalOrBug(memberId, images, imageNumber);

        List<ProposalOrBugReportImage> proposalOrBugReportImages;
        if (imageNumber == null) {
            proposalOrBugReportImages = List.of();
        } else {
            List<ReportImagePath> reportImagePaths =
                    memberImageIOHelper.uploadImage(memberId, reportId, images)
                            .stream()
                            .map(ReportImagePath::create).toList();

            List<ProposalOrBugReportImageFileName> proposalOrBugReportImageFileNames =
                    images.stream().map(element -> ProposalOrBugReportImageFileName.create(element.getOriginalFilename())).toList();

            proposalOrBugReportImages = new ArrayList<>();
            for (int i = 0; i < imageNumber; i++){
                proposalOrBugReportImages.add(
                        ProposalOrBugReportImage.create(
                                reportImagePaths.get(i),
                                proposalOrBugReportImageFileNames.get(i),
                                ReportImageBytes.create(null)));
            }
        }

        reportRepository.reportProposalOrBug(
                memberId,
                ProposalOrBugReport.create(
                        reportId, reportTitle, reportContent, proposalOrBugReportImages
                ));
    }

    public void reportPostAbuse(PostAbuseReportRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        validateBeforeReportPostAbuse(memberId, targetPostId);

        reportRepository.reportPostAbuse(memberId, targetPostId);
    }

    public void reportCommentAbuse(CommentAbuseReportRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetCommentId targetCommentId = TargetCommentId.create(
                TargetPostId.create(record.postUlid()), TargetCommentPath.create(record.path()));
        validateBeforeReportCommentAbuse(memberId, targetCommentId);

        reportRepository.reportCommentAbuse(memberId, targetCommentId);
    }

    public void withdraw(MemberWithdrawalRecord record) {
        String accessToken = record.accessToken();
        String authCode = record.authCode();
        String authProvider = record.authProvider();
        MemberId memberId = MemberId.fromUuid(jwtTokenProvider.getMemberUuidFromToken(accessToken));
        validateBeforeWithdraw(memberId, authCode, authProvider);

        if (authCode != null && authProvider != null) {
            memberSocialTranslator.deleteSocialAccountWithSocialAccessToken(
                    memberSocialTranslator.getSocialAccessToken(authCode, authProvider),
                    authProvider,
                    memberId.getValue());
        }
        tokenService.blacklistAccessToken(accessToken);
        memberRepository.withdraw(memberId, record.reason(), MemberWithdrawOpinion.create(record.opinion()));
    }

    private void validateBeforeOverrideProfile(MemberId memberId, Nickname nickname) {
        memberValidationHelper.validateIfMemberExists(memberId);
        if (swearService.isSwearContained(nickname.getValue())) {
            throw new SwearContainedException();
        }
        Optional<Member> emptyOrMember = memberRepository.getByNickname(nickname);
        if (emptyOrMember.isPresent() && !emptyOrMember.orElseThrow().getMemberId().equals(memberId)) {
            throw new ExistsEntityException(KernelErrorCode.EXISTS_NICKNAME, "nickname");
        }
    }

    private void validateBeforeLikePost(MemberId memberId, TargetPostId targetPostId) {
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetPostExists(targetPostId);
        if (!targetPostRepository.isPublished(targetPostId)) {
            throw new NotAccessibleException(NOT_ACCESSIBLE_POST_LIKE, "postLike", targetPostId.getValue());
        }
    }

    private void validateBeforeUnlikePost(MemberId memberId, TargetPostId targetPostId) {
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetPostExists(targetPostId);
        if (!targetPostRepository.isPublished(targetPostId)) {
            throw new NotAccessibleException(NOT_ACCESSIBLE_POST_LIKE, "postUnlike", targetPostId.getValue());
        }
    }

    private void validateBeforeBookmarkOrCancelPostBookmark(MemberId memberId, TargetPostId targetPostId) {
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetPostExists(targetPostId);
        if (!targetPostRepository.isPublished(targetPostId)) {
            throw new NotAccessibleException(NOT_ACCESSIBLE_POST_BOOKMARK, "postBookmark", targetPostId.getValue());
        }
    }

    private void validateBeforeReportProposalOrBug(MemberId memberId, List<MultipartFile> images, Integer imageNumber) {
        memberValidationHelper.validateIfMemberExists(memberId);
        if ((images == null && imageNumber != null) || (images != null && imageNumber == null)) {
            throw new InvalidValueException(MISMATCHED_REPORT_IMAGE_SIZE, List.of("images", "imageNumber"));
        } else if (images != null && images.size() != imageNumber) {
            throw new InvalidValueException(MISMATCHED_REPORT_IMAGE_SIZE, List.of("images", "imageNumber"));
        }
    }

    private void validateBeforeReportPostAbuse(MemberId memberId, TargetPostId targetPostId) {
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetPostExists(targetPostId);
        if (!targetPostRepository.isPublished(targetPostId)) {
            throw new NotAccessibleException(
                    NOT_ACCESSIBLE_POST_REPORT_FOR_ABUSE, "postReportForAbuse", targetPostId.getValue());
        } else if (reportRepository.isMemberAbusePost(memberId, targetPostId)) {
            throw new ExistsEntityException(
                    EntityErrorCode.EXISTS_POST_ABUSE_REPORT, "postAbuseReport");
        }
    }

    private void validateBeforeReportCommentAbuse(MemberId memberId, TargetCommentId targetCommentId) {
        memberValidationHelper.validateIfMemberExists(memberId);
        memberValidationHelper.validateIfTargetCommentExists(targetCommentId);
        if (reportRepository.isMemberAbuseComment(memberId, targetCommentId)) {
            throw new ExistsEntityException(EntityErrorCode.EXISTS_COMMENT_ABUSE_REPORT, "commentAbuseReport");
        }
    }

    private void validateBeforeWithdraw(MemberId memberId, String authCode, String authProvider) {
        memberValidationHelper.validateIfMemberExists(memberId);
        if ((authCode != null && authProvider == null) || (authCode == null && authProvider != null)) {
            throw new InvalidValueException(MISMATCHED_AUTH_INFO, List.of("authCode", "authProvider"));
        }
    }
}
