package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.adapter.helper.MemberImageIOHelper;
import kr.modusplant.domains.member.adapter.helper.MemberValidationHelper;
import kr.modusplant.domains.member.adapter.translator.MemberSocialTranslator;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.entity.ReportImage;
import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyReportImageBytes;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.domains.member.usecase.port.repository.*;
import kr.modusplant.domains.member.usecase.record.*;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import kr.modusplant.infrastructure.jwt.service.TokenService;
import kr.modusplant.infrastructure.swear.exception.SwearContainedException;
import kr.modusplant.infrastructure.swear.service.SwearService;
import kr.modusplant.shared.event.*;
import kr.modusplant.shared.exception.InvalidFileInputException;
import kr.modusplant.shared.exception.InvalidValueException;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import kr.modusplant.shared.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.shared.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
        memberValidationHelper.validateIfMemberExists(memberId);
        validateBeforeReportProposalOrBug(images, imageNumber);

        List<ReportImage> reportImages;
        if (imageNumber == null) {
            reportImages = List.of();
        } else {
            List<ReportImagePath> reportImagePaths =
                    memberImageIOHelper.uploadImage(memberId, reportId, images)
                            .stream()
                            .map(ReportImagePath::create).toList();
            List<ReportImageFileName> reportImageFileNames =
                    images.stream().map(element -> ReportImageFileName.create(element.getOriginalFilename())).toList();
            reportImages = new ArrayList<>();
            for (int i = 0; i < imageNumber; i++){
                reportImages.add(
                        ReportImage.create(
                                reportImagePaths.get(i), reportImageFileNames.get(i), EmptyReportImageBytes.create()));
            }
        }
        applicationEventPublisher.publishEvent(
                ProposalOrBugReportEvent.create(
                        memberId.getValue(),
                        reportId.getValue(),
                        reportTitle.getValue(),
                        reportContent.getValue(),
                        reportImages.stream().map(element -> element.getReportImageFileName().getFileName()).toList(),
                        reportImages.stream().map(element -> element.getReportImagePath().getValue()).toList()
                ));
    }

    public void reportPostAbuse(PostAbuseReportRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        validateBeforeReportPostAbuse(memberId, targetPostId);

        applicationEventPublisher.publishEvent(
                PostAbuseReportEvent.create(memberId.getValue(), record.postUlid()));
    }

    public void reportCommentAbuse(CommentAbuseReportRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetCommentId targetCommentId = TargetCommentId.create(
                TargetPostId.create(record.postUlid()), TargetCommentPath.create(record.path()));
        validateBeforeReportCommentAbuse(memberId, targetCommentId);

        applicationEventPublisher.publishEvent(
                CommentAbuseReportEvent.create(memberId.getValue(), record.postUlid(), record.path()));
    }

    public void withdraw(MemberWithdrawalRecord record) {
        String accessToken = record.accessToken();
        String authCode = record.authCode();
        String authProvider = record.authProvider();
        MemberId memberId = MemberId.fromUuid(jwtTokenProvider.getMemberUuidFromToken(accessToken));
        validateBeforeWithdraw(record, memberId, authCode, authProvider);

        if (authCode != null && authProvider != null) {
            memberSocialTranslator.deleteSocialAccountWithSocialAccessToken(
                    memberSocialTranslator.getSocialAccessToken(authCode, authProvider),
                    authProvider,
                    memberId.getValue());
        }
        tokenService.blacklistAccessToken(accessToken);
        applicationEventPublisher.publishEvent(
                MemberWithdrawalEvent.create(memberId.getValue(), record.reason().name(), record.opinion()));
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

    private void validateBeforeReportProposalOrBug(List<MultipartFile> images, Integer imageNumber) {
        if ((images == null && imageNumber != null) || (images != null && imageNumber == null)) {
            throw new InvalidValueException(MISMATCHED_REPORT_IMAGE_SIZE, List.of("images", "imageNumber"));
        } else if (images != null && images.size() != imageNumber) {
            throw new InvalidValueException(MISMATCHED_REPORT_IMAGE_SIZE, List.of("images", "imageNumber"));
        } else if (images != null) {
            for (int i = 0; i < imageNumber; i++) {
                String originalFilename = images.get(i).getOriginalFilename();
                if (StringUtils.isBlank(originalFilename)) {
                    throw new InvalidFileInputException();
                } else if (!originalFilename.contains("image_" + i)) {  // 파일 이름은 image_0.확장자 ~ image_2.확장자로 강제
                    log.error(originalFilename);
                    throw new InvalidValueException(INVALID_REPORT_IMAGE_NAME, "originalFilename");
                }
            }
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

    private void validateBeforeWithdraw(MemberWithdrawalRecord record, MemberId memberId, String authCode, String authProvider) {
        memberValidationHelper.validateIfMemberExists(memberId);
        if (record.opinion().length() > 600) {
            throw new InvalidValueException(MemberErrorCode.MEMBER_WITHDRAW_OPINION_OVER_LENGTH, "opinion");
        }
        if ((authCode != null && authProvider == null) || (authCode == null && authProvider != null)) {
            throw new InvalidValueException(GeneralErrorCode.INVALID_INPUT, List.of("authCode", "authProvider"));
        }
    }
}
