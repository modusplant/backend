package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.exception.AlreadyLikedException;
import kr.modusplant.domains.member.domain.exception.AlreadyUnlikedException;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberNickname;
import kr.modusplant.domains.member.domain.vo.TargetPostId;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.domains.member.usecase.port.repository.TargetPostIdRepository;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.PostLikeEvent;
import kr.modusplant.shared.event.PostUnlikeEvent;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberController {
    private final MemberMapper mapper;
    private final MemberRepository memberRepository;
    private final TargetPostIdRepository targetPostIdRepository;
    private final EventBus eventBus;

    public MemberResponse register(MemberNickname nickname) {
        Member member = Member.create(nickname);
        validateBeforeRegister(member);
        return mapper.toMemberResponse(memberRepository.save(member));
    }

    public MemberResponse updateNickname(MemberId memberId, MemberNickname memberNickname) {
        Member member = Member.create(memberId, memberNickname);
        validateBeforeUpdateNickname(member);
        return mapper.toMemberResponse(memberRepository.save(member));
    }

    public void likePost(UUID memberId, String postUlid) {
        validateBeforeLikePost(memberId, postUlid);
        eventBus.publish(PostLikeEvent.create(memberId, postUlid));
    }

    public void unlikePost(UUID memberId, String postUlid) {
        validateBeforeUnlikePost(memberId, postUlid);
        eventBus.publish(PostUnlikeEvent.create(memberId, postUlid));
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

    private void validateBeforeLikePost(UUID memberId, String postUlid) {
        if (!memberRepository.isIdExist(MemberId.fromUuid(memberId))) {
            throw new EntityNotFoundException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        if (!targetPostIdRepository.isIdExist(TargetPostId.create(postUlid))) {
            throw new EntityNotFoundException(NOT_FOUND_TARGET_POST_ID, "targetPostId");
        }
        if (targetPostIdRepository.isLiked(MemberId.fromUuid(memberId), TargetPostId.create(postUlid))) {
            throw new AlreadyLikedException();
        }
    }

    private void validateBeforeUnlikePost(UUID memberId, String postUlid) {
        if (!memberRepository.isIdExist(MemberId.fromUuid(memberId))) {
            throw new EntityNotFoundException(NOT_FOUND_MEMBER_ID, "memberId");
        }
        if (!targetPostIdRepository.isIdExist(TargetPostId.create(postUlid))) {
            throw new EntityNotFoundException(NOT_FOUND_TARGET_POST_ID, "targetPostId");
        }
        if (targetPostIdRepository.isUnliked(MemberId.fromUuid(memberId), TargetPostId.create(postUlid))) {
            throw new AlreadyUnlikedException();
        }
    }
}
