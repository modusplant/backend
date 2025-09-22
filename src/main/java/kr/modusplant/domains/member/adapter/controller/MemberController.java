package kr.modusplant.domains.member.adapter.controller;

import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.exception.AlreadyExistedNicknameException;
import kr.modusplant.domains.member.usecase.port.mapper.MemberMapper;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.domains.member.usecase.request.MemberNicknameUpdateRequest;
import kr.modusplant.domains.member.usecase.request.MemberRegisterRequest;
import kr.modusplant.domains.member.usecase.response.MemberResponse;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.PostLikeEvent;
import kr.modusplant.shared.event.PostUnlikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberController {
    private final MemberMapper mapper;
    private final MemberRepository memberRepository;
    private final EventBus eventBus;

    public MemberResponse register(MemberRegisterRequest request) {
        Member member = mapper.toMember(request);
        validateMemberBeforeRegister(member);
        return mapper.toMemberResponse(memberRepository.save(member));
    }

    public MemberResponse updateNickname(MemberNicknameUpdateRequest request) {
        Member member = mapper.toMember(request);
        validateMemberBeforeUpdateNickname(member);
        return mapper.toMemberResponse(memberRepository.updateNickname(member));
    }

    public void likePost(UUID memberId, String postUlid) {
        eventBus.publish(PostLikeEvent.create(memberId, postUlid));
    }

    public void unlikePost(UUID memberId, String postUlid) {
        eventBus.publish(PostUnlikeEvent.create(memberId, postUlid));
    }

    private void validateMemberBeforeRegister(Member member) {
        if (memberRepository.isNicknameExist(member.getMemberNickname())) {
            throw new AlreadyExistedNicknameException();
        }
    }

    private void validateMemberBeforeUpdateNickname(Member member) {
        Optional<Member> emptyOrMember = memberRepository.getByNickname(member.getMemberNickname());
        if (emptyOrMember.isPresent() && !emptyOrMember.orElseThrow().getMemberId().equals(member.getMemberId())) {
            throw new AlreadyExistedNicknameException();
        }
    }
}
