package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.aggregate.MemberProfile;
import kr.modusplant.domains.member.domain.entity.MemberProfileImage;
import kr.modusplant.domains.member.domain.entity.nullobject.MemberEmptyProfileImage;
import kr.modusplant.domains.member.domain.exception.NotAccessiblePostLikeException;
import kr.modusplant.domains.member.domain.vo.*;
import kr.modusplant.domains.member.domain.vo.nullobject.MemberEmptyProfileIntroduction;
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
import kr.modusplant.framework.out.aws.service.S3FileService;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.CommentLikeEvent;
import kr.modusplant.shared.event.CommentUnlikeEvent;
import kr.modusplant.shared.event.PostLikeEvent;
import kr.modusplant.shared.event.PostUnlikeEvent;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static kr.modusplant.domains.member.adapter.util.MemberProfileImageUtils.generateMemberProfileImagePath;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class MemberController {
    private final S3FileService s3FileService;
    private final MemberMapper memberMapper;
    private final MemberProfileMapper memberProfileMapper;
    private final MemberRepository memberRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final TargetPostIdRepository targetPostIdRepository;
    private final TargetCommentIdRepository targetCommentIdRepository;
    private final EventBus eventBus;

    public MemberResponse register(MemberRegisterRequest request) {
        MemberNickname memberNickname = MemberNickname.create(request.nickname());
        validateBeforeRegister(memberNickname);
        return memberMapper.toMemberResponse(memberRepository.save(memberNickname));
    }

    public MemberProfileResponse getProfile(MemberProfileGetRecord record) throws IOException {
        MemberId memberId = MemberId.fromUuid(record.id());
        Optional<Member> optionalMember = memberRepository.getById(memberId);
        if (optionalMember.isEmpty()) {
            throw new EntityNotFoundException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        Optional<MemberProfile> optionalMemberProfile = memberProfileRepository.getById(memberId);
        if (optionalMemberProfile.isPresent()) {
            return memberProfileMapper.toMemberProfileResponse(optionalMemberProfile.orElseThrow());
        } else {
            return new MemberProfileResponse(memberId.getValue(), null, null, optionalMember.orElseThrow().getMemberNickname().getValue());
        }
    }

    public MemberProfileResponse overrideProfile(MemberProfileOverrideRecord record) throws IOException {
        MemberId memberId = MemberId.fromUuid(record.id());
        MemberNickname memberNickname = MemberNickname.create(record.nickname());
        validateBeforeOverrideProfile(memberId, memberNickname);
        MemberProfile memberProfile;
        MemberProfileImage memberProfileImage;
        MemberProfileIntroduction memberProfileIntroduction;
        boolean isImageExist = !(record.image() == null);
        boolean isIntroductionExist = !(record.introduction() == null);
        Optional<MemberProfile> optionalMemberProfile = memberProfileRepository.getById(memberId);
        if (optionalMemberProfile.isPresent()) {
            memberProfile = optionalMemberProfile.orElseThrow();
            String imagePath = memberProfile.getMemberProfileImage().getMemberProfileImagePath().getValue();
            if (imagePath != null) {
                s3FileService.deleteFiles(imagePath);
            }
        } else {
            log.warn("Not found member profile, member uuid: {}. Please check it out. ", memberId.getValue());
        }
        if (isImageExist) {
            String newImagePath = uploadImage(memberId, record);
            memberProfileImage = MemberProfileImage.create(
                    MemberProfileImagePath.create(newImagePath),
                    MemberProfileImageBytes.create(record.image().getBytes())
            );
        } else {
            memberProfileImage = MemberEmptyProfileImage.create();
        }
        if (isIntroductionExist) {
            memberProfileIntroduction = MemberProfileIntroduction.create(record.introduction());
        } else {
            memberProfileIntroduction = MemberEmptyProfileIntroduction.create();
        }
        memberProfile = MemberProfile.create(memberId, memberProfileImage, memberProfileIntroduction, memberNickname);
        return memberProfileMapper.toMemberProfileResponse(memberProfileRepository.addOrUpdate(memberProfile));
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

    private void validateBeforeRegister(MemberNickname memberNickname) {
        if (memberRepository.isNicknameExist(memberNickname)) {
            throw new EntityExistsException(ALREADY_EXISTED_NICKNAME, "memberNickname");
        }
    }

    private void validateBeforeOverrideProfile(MemberId memberId, MemberNickname memberNickname) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new EntityNotFoundException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        Optional<Member> emptyOrMember = memberRepository.getByNickname(memberNickname);
        if (emptyOrMember.isPresent() && !emptyOrMember.orElseThrow().getMemberId().equals(memberId)) {
            throw new EntityExistsException(ALREADY_EXISTED_NICKNAME, "memberNickname");
        }
    }

    private void validateBeforeLikeOrUnlikePost(MemberId memberId, TargetPostId targetPostId) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new EntityNotFoundException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        if (!targetPostIdRepository.isIdExist(targetPostId)) {
            throw new EntityNotFoundException(NOT_FOUND_TARGET_POST_ID, "targetPostId");
        }
        if (!targetPostIdRepository.isPublished(targetPostId)) {
            throw new NotAccessiblePostLikeException();
        }
    }

    private void validateBeforeLikeOrUnlikeComment(MemberId memberId, TargetCommentId targetCommentId) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new EntityNotFoundException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        if (!targetCommentIdRepository.isIdExist(targetCommentId)) {
            throw new EntityNotFoundException(NOT_FOUND_TARGET_COMMENT_ID, "targetCommentId");
        }
    }

    private String uploadImage(MemberId memberId, MemberProfileOverrideRecord record) throws IOException {
        String newImagePath = generateMemberProfileImagePath(memberId.getValue(), record.image().getOriginalFilename());
        s3FileService.uploadFile(record.image(), newImagePath);
        return newImagePath;
    }

}
