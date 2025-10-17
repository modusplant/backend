package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.exception.CommentAlreadyLikedException;
import kr.modusplant.domains.member.domain.exception.CommentAlreadyUnlikedException;
import kr.modusplant.domains.member.domain.exception.PostAlreadyLikedException;
import kr.modusplant.domains.member.domain.exception.PostAlreadyUnlikedException;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.TargetCommentId;
import kr.modusplant.domains.member.domain.vo.TargetCommentPath;
import kr.modusplant.domains.member.domain.vo.TargetPostId;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetCommentIdRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetPostIdRepository;
import kr.modusplant.domains.member.usecase.request.*;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.CommentLikeEvent;
import kr.modusplant.shared.event.CommentUnlikeEvent;
import kr.modusplant.shared.event.PostLikeEvent;
import kr.modusplant.shared.event.PostUnlikeEvent;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberController {
    private final MemberMapper mapper;
    private final MemberRepository memberRepository;
    private final TargetPostIdRepository targetPostIdRepository;
    private final TargetCommentIdRepository targetCommentIdRepository;
    private final EventBus eventBus;

    public MemberResponse register(MemberRegisterRequest request) {
        Member member = mapper.toMember(request);
        validateBeforeRegister(member);
        return mapper.toMemberResponse(memberRepository.save(member));
    }

    public MemberResponse updateNickname(MemberNicknameUpdateRequest request) {
        Member member = mapper.toMember(request);
        validateBeforeUpdateNickname(member);
        return mapper.toMemberResponse(memberRepository.save(member));
    }

    public void likePost(MemberPostLikeRequest request) {
        MemberId memberId = MemberId.fromUuid(request.memberId());
        TargetPostId targetPostId = TargetPostId.create(request.postUlid());
        validateBeforeLikePost(memberId, targetPostId);
        eventBus.publish(PostLikeEvent.create(memberId.getValue(), targetPostId.getValue()));
    }

    public void unlikePost(MemberPostUnlikeRequest request) {
        MemberId memberId = MemberId.fromUuid(request.memberId());
        TargetPostId targetPostId = TargetPostId.create(request.postUlid());
        validateBeforeUnlikePost(memberId, targetPostId);
        eventBus.publish(PostUnlikeEvent.create(memberId.getValue(), targetPostId.getValue()));
    }

    public void likeComment(MemberCommentLikeRequest request) {
        MemberId memberId = MemberId.fromUuid(request.memberId());
        TargetCommentId targetCommentId = TargetCommentId.create(TargetPostId.create(request.postUlid()), TargetCommentPath.create(request.path()));
        validateBeforeLikeComment(memberId, targetCommentId);
        eventBus.publish(CommentLikeEvent.create(memberId.getValue(), targetCommentId.getTargetPostId().getValue(), targetCommentId.getTargetCommentPath().getValue()));
    }

    public void unlikeComment(MemberCommentUnlikeRequest request) {
        MemberId memberId = MemberId.fromUuid(request.memberId());
        TargetCommentId targetCommentId = TargetCommentId.create(TargetPostId.create(request.postUlid()), TargetCommentPath.create(request.path()));
        validateBeforeUnlikeComment(memberId, targetCommentId);
        eventBus.publish(CommentUnlikeEvent.create(memberId.getValue(), targetCommentId.getTargetPostId().getValue(), targetCommentId.getTargetCommentPath().getValue()));
    }

    private void validateBeforeRegister(Member member) {
        if (memberRepository.isNicknameExist(member.getMemberNickname())) {
            throw new EntityExistsException(ALREADY_EXISTED_NICKNAME, "memberNickname");
        }
    }

    private void validateBeforeUpdateNickname(Member member) {
        Optional<Member> emptyOrMember = memberRepository.getByNickname(member.getMemberNickname());
        if (emptyOrMember.isPresent() && !emptyOrMember.orElseThrow().getMemberId().equals(member.getMemberId())) {
            throw new EntityExistsException(ALREADY_EXISTED_NICKNAME, "memberNickname");
        }
    }

    private void validateBeforeLikePost(MemberId memberId, TargetPostId targetPostId) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new EntityNotFoundException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        if (!targetPostIdRepository.isIdExist(targetPostId)) {
            throw new EntityNotFoundException(NOT_FOUND_TARGET_POST_ID, "targetPostId");
        }
        if (targetPostIdRepository.isLiked(memberId, targetPostId)) {
            throw new PostAlreadyLikedException();
        }
    }

    private void validateBeforeUnlikePost(MemberId memberId, TargetPostId targetPostId) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new EntityNotFoundException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        if (!targetPostIdRepository.isIdExist(targetPostId)) {
            throw new EntityNotFoundException(NOT_FOUND_TARGET_POST_ID, "targetPostId");
        }
        if (targetPostIdRepository.isUnliked(memberId, targetPostId)) {
            throw new PostAlreadyUnlikedException();
        }
    }

    private void validateBeforeLikeComment(MemberId memberId, TargetCommentId targetCommentId) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new EntityNotFoundException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        if (!targetCommentIdRepository.isIdExist(targetCommentId)) {
            throw new EntityNotFoundException(NOT_FOUND_TARGET_COMMENT_ID, "targetCommentId");
        }
        if (targetCommentIdRepository.isLiked(memberId, targetCommentId)) {
            throw new CommentAlreadyLikedException();
        }
    }

    private void validateBeforeUnlikeComment(MemberId memberId, TargetCommentId targetCommentId) {
        if (!memberRepository.isIdExist(memberId)) {
            throw new EntityNotFoundException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        if (!targetCommentIdRepository.isIdExist(targetCommentId)) {
            throw new EntityNotFoundException(NOT_FOUND_TARGET_COMMENT_ID, "targetCommentId");
        }
        if (targetCommentIdRepository.isUnliked(memberId, targetCommentId)) {
            throw new CommentAlreadyUnlikedException();
        }
    }
}
