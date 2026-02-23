package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.entity.nullobject.EmptyMemberProfileImage;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyReportImagePath;
import kr.modusplant.domains.member.domain.vo.nullobject.EmptyMemberProfileIntroduction;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import kr.modusplant.domains.member.usecase.port.mapper.MemberProfileMapper;
import kr.modusplant.domains.member.usecase.port.repository.MemberProfileRepository;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetCommentIdRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetPostIdRepository;
import kr.modusplant.domains.member.usecase.record.*;
import kr.modusplant.domains.member.usecase.request.MemberRegisterRequest;
import kr.modusplant.domains.member.usecase.response.MemberProfileResponse;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import kr.modusplant.framework.aws.service.S3FileService;
import kr.modusplant.framework.jpa.exception.ExistsEntityException;
import kr.modusplant.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.framework.jpa.exception.enums.EntityErrorCode;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.infrastructure.swear.exception.SwearContainedException;
import kr.modusplant.infrastructure.swear.service.SwearService;
import kr.modusplant.shared.event.*;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.kernel.Nickname;
import kr.modusplant.shared.kernel.enums.KernelErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static kr.modusplant.domains.member.adapter.util.MemberProfileImageUtils.generateMemberProfileImagePath;
import static kr.modusplant.domains.member.adapter.util.MemberProfileImageUtils.generateReportImagePath;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;

@SuppressWarnings("LoggingSimilarMessage")
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class MemberController {
    private final S3FileService s3FileService;
    private final SwearService swearService;
    private final MemberMapper memberMapper;
    private final MemberProfileMapper memberProfileMapper;
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final TargetPostIdRepository targetPostIdRepository;
    private final TargetCommentIdRepository targetCommentIdRepository;
    private final EventBus eventBus;

    public MemberResponse registerMember(MemberRegisterRequest request) {
        Nickname nickname = Nickname.create(request.nickname());
        validateBeforeRegister(nickname);
        return memberMapper.toMemberResponse(memberRepository.add(nickname));
    }

    @Transactional(readOnly = true)
    public boolean checkExistedNickname(MemberNicknameCheckRecord record) {
        Nickname nickname = Nickname.create(record.nickname());
        return memberRepository.isNicknameExist(nickname);
    }

    @Transactional(readOnly = true)
    public MemberProfileResponse getProfile(MemberProfileGetRecord record) throws IOException {
        MemberId memberId = MemberId.fromUuid(record.id());
        Optional<Member> optionalMember = memberRepository.getById(memberId);
        if (optionalMember.isEmpty()) {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER_ID, "memberId");
        }
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
            memberProfile = optionalMemberProfile.orElseThrow();
            String imagePath = memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue();
            if (imagePath != null) {
                s3FileService.deleteFiles(imagePath);
            }
        } else {
            throw new NotFoundEntityException(EntityErrorCode.NOT_FOUND_MEMBER_PROFILE, "memberProfile");
        }
        if (!(image == null)) {
            String newImagePath = uploadImage(memberId, record);
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
        validateBeforeLikeOrUnlikePost(memberId, targetPostId);
        if (targetPostIdRepository.isUnliked(memberId, targetPostId)) {
            eventBus.publish(PostLikeEvent.create(memberId.getValue(), targetPostId.getValue()));
        }
    }

    public void unlikePost(MemberPostUnlikeRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        validateBeforeLikeOrUnlikePost(memberId, targetPostId);
        if (targetPostIdRepository.isLiked(memberId, targetPostId)) {
            eventBus.publish(PostUnlikeEvent.create(memberId.getValue(), targetPostId.getValue()));
        }
    }

    public void bookmarkPost(MemberPostBookmarkRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        validateBeforeBookmarkOrCancelBookmark(memberId, targetPostId);
        if (targetPostIdRepository.isNotBookmarked(memberId, targetPostId)) {
            eventBus.publish(PostBookmarkEvent.create(memberId.getValue(), targetPostId.getValue()));
        }
    }

    public void cancelPostBookmark(MemberPostBookmarkCancelRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetPostId targetPostId = TargetPostId.create(record.postUlid());
        validateBeforeBookmarkOrCancelBookmark(memberId, targetPostId);
        if (targetPostIdRepository.isBookmarked(memberId, targetPostId)) {
            eventBus.publish(PostBookmarkCancelEvent.create(memberId.getValue(), targetPostId.getValue()));
        }
    }

    public void likeComment(MemberCommentLikeRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetCommentId targetCommentId = TargetCommentId.create(TargetPostId.create(record.postUlid()), TargetCommentPath.create(record.path()));
        validateBeforeLikeOrUnlikeComment(memberId, targetCommentId);
        if (targetCommentIdRepository.isUnliked(memberId, targetCommentId)) {
            eventBus.publish(CommentLikeEvent.create(memberId.getValue(), targetCommentId.getTargetPostId().getValue(), targetCommentId.getTargetCommentPath().getValue()));
        }
    }

    public void unlikeComment(MemberCommentUnlikeRecord record) {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        TargetCommentId targetCommentId = TargetCommentId.create(TargetPostId.create(record.postUlid()), TargetCommentPath.create(record.path()));
        validateBeforeLikeOrUnlikeComment(memberId, targetCommentId);
        if (targetCommentIdRepository.isLiked(memberId, targetCommentId)) {
            eventBus.publish(CommentUnlikeEvent.create(memberId.getValue(), targetCommentId.getTargetPostId().getValue(), targetCommentId.getTargetCommentPath().getValue()));
        }
    }

    public void reportProposalOrBug(ProposalOrBugReportRecord record) throws IOException {
        MemberId memberId = MemberId.fromUuid(record.memberId());
        ReportTitle reportTitle = ReportTitle.create(record.title());
        ReportContent reportContent = ReportContent.create(record.content());
        ReportImagePath reportImagePath;
        MultipartFile image = record.image();
        if (!memberRepository.isIdExist(memberId)) {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER_ID, "memberId");
        };
        if (!(image == null)) {
            reportImagePath = ReportImagePath.create(uploadImage(memberId, record));
        } else {
            reportImagePath = EmptyReportImagePath.create();
        }
        eventBus.publish(ProposalOrBugReportEvent.create(memberId.getValue(), reportTitle.getValue(), reportContent.getValue(), reportImagePath.getValue()));
    }

    private void validateBeforeRegister(Nickname nickname) {
        if (memberRepository.isNicknameExist(nickname)) {
            throw new ExistsEntityException(KernelErrorCode.EXISTS_NICKNAME, "nickname");
        }
    }

    private void validateBeforeOverrideProfile(MemberId memberId, Nickname memberNickname) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        if (swearService.isSwearContained(memberNickname.getValue())) {
            throw new SwearContainedException();
        }
        Optional<Member> emptyOrMember = memberRepository.getByNickname(memberNickname);
        if (emptyOrMember.isPresent() && !emptyOrMember.orElseThrow().getMemberId().equals(memberId)) {
            throw new ExistsEntityException(KernelErrorCode.EXISTS_NICKNAME, "memberNickname");
        }
    }

    private void validateBeforeLikeOrUnlikePost(MemberId memberId, TargetPostId targetPostId) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        if (!targetPostIdRepository.isIdExist(targetPostId)) {
            throw new NotFoundEntityException(NOT_FOUND_TARGET_POST_ID, "targetPostId");
        }
        if (!targetPostIdRepository.isPublished(targetPostId)) {
            throw new NotAccessibleException(NOT_ACCESSIBLE_POST_LIKE, "postLike", targetPostId.getValue());
        }
    }

    private void validateBeforeBookmarkOrCancelBookmark(MemberId memberId, TargetPostId targetPostId) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        if (!targetPostIdRepository.isIdExist(targetPostId)) {
            throw new NotFoundEntityException(NOT_FOUND_TARGET_POST_ID, "targetPostId");
        }
        if (!targetPostIdRepository.isPublished(targetPostId)) {
            throw new NotAccessibleException(NOT_ACCESSIBLE_POST_BOOKMARK, "postBookmark", targetPostId.getValue());
        }
    }

    private void validateBeforeLikeOrUnlikeComment(MemberId memberId, TargetCommentId targetCommentId) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        if (!targetCommentIdRepository.isIdExist(targetCommentId)) {
            throw new NotFoundEntityException(NOT_FOUND_TARGET_COMMENT_ID, "targetCommentId");
        }
    }

    private String uploadImage(MemberId memberId, MemberProfileOverrideRecord record) throws IOException {
        String newImagePath = generateMemberProfileImagePath(memberId.getValue(), record.image().getOriginalFilename());
        s3FileService.uploadFile(record.image(), newImagePath);
        return newImagePath;
    }

    private String uploadImage(MemberId memberId, ProposalOrBugReportRecord record) throws IOException {
        String newImagePath = generateReportImagePath(memberId.getValue(), record.image().getOriginalFilename());
        s3FileService.uploadFile(record.image(), newImagePath);
        return newImagePath;
    }
}
