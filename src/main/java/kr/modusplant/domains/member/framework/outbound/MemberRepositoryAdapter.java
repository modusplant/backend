package kr.modusplant.domains.member.framework.outbound;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.enums.MemberWithdrawReason;
import kr.modusplant.domains.member.domain.event.RecentlyViewPostRemoveEvent;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberWithdrawOpinion;
import kr.modusplant.domains.member.framework.outbound.jooq.record.ActivitySubjectCommentIdRecord;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ActivitySubjectCommentJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ActivitySubjectPostJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.MemberJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberWithdrawEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.mapper.supers.MemberJpaMapper;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.*;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.domains.post.framework.outbound.jooq.repository.PostJooqRepository;
import kr.modusplant.shared.framework.aws.event.ImageRemoveTask;
import kr.modusplant.shared.framework.aws.event.ImagesRemoveTask;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.kernel.Nickname;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_MEMBER;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepository {
    private final StringRedisTemplate stringRedisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    private final MemberJpaMapper memberJpaMapper;
    private final MemberJpaRepository memberJpaRepository;

    private final PostLikeJpaRepository postLikeJpaRepository;
    private final CommentLikeJpaRepository commentLikeJpaRepository;
    private final MemberWithdrawJpaRepository memberWithdrawJpaRepository;

    private final ActivitySubjectPostJooqRepository activitySubjectPostJooqRepository;
    private final ActivitySubjectCommentJooqRepository activitySubjectCommentJooqRepository;
    private final MemberJooqRepository memberJooqRepository;
    private final MemberProfileJpaRepository memberProfileJpaRepository;
    private final PostJooqRepository postJooqRepository;

    @Override
    public Member getById(MemberId memberId) {
        Optional<MemberEntity> emptyOrMemberEntity = memberJpaRepository.findByUuid(memberId.getValue());
        if (emptyOrMemberEntity.isPresent()) {
            return memberJpaMapper.toMember(emptyOrMemberEntity.orElseThrow());
        } else {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER, "member");
        }
    }

    @Override
    public Optional<Member> getByNickname(Nickname nickname) {
        Optional<MemberEntity> emptyOrMemberEntity = memberJpaRepository.findByNickname(nickname.getValue());
        return emptyOrMemberEntity.isEmpty() ?
                Optional.empty() : Optional.of(memberJpaMapper.toMember(emptyOrMemberEntity.orElseThrow()));
    }

    @Override
    public Member add(Nickname nickname) {
        return memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toMemberEntity(nickname)));
    }

    @Override
    public Member add(MemberId memberId, Nickname nickname) {
        return memberJpaMapper.toMember(memberJpaRepository.save(memberJpaMapper.toMemberEntity(memberId, nickname)));
    }

    @Override
    public boolean isIdExist(MemberId memberId) {
        return memberJpaRepository.existsByUuid(memberId.getValue());
    }

    @Override
    public boolean isNicknameExist(Nickname nickname) {
        return memberJpaRepository.existsByNickname(nickname.getValue());
    }

    @Override
    public void withdraw(MemberId memberIdVO, MemberWithdrawReason reasonEnum, MemberWithdrawOpinion opinionVO) {
        UUID memberId = memberIdVO.getValue();
        String reason = reasonEnum.name();
        String opinion = opinionVO.getValue();

        stringRedisTemplate.unlink("recentlyView:member:%s:posts".formatted(memberId));     // 최근에 본 게시글 데이터 삭제

        String[] publishedPostUlids = postJooqRepository.getPublishedPostUlidsByMemberId(memberId);
        List<JsonNode> publishedPostContents =
                postJooqRepository.getPostContentsFromPublishedPostUlids(publishedPostUlids);

        List<String> fileKeysToDelete = new ArrayList<>();      // 컨텐츠로부터 파일 키 수집
        for (JsonNode content : publishedPostContents) {
            if (content == null || !content.isArray()) {
                continue;
            }
            for (JsonNode node : content) {
                if (node.has("src")) {
                    fileKeysToDelete.add(node.get("src").asText());
                }
            }
        }
        if (!fileKeysToDelete.isEmpty()) {
            eventPublisher.publishEvent(ImagesRemoveTask.create(fileKeysToDelete));
        }
        if (!ArrayUtils.isEmpty(publishedPostUlids)) {
            eventPublisher.publishEvent(RecentlyViewPostRemoveEvent.create(publishedPostUlids));
        }

        if (publishedPostUlids.length != 0) {       // 게시글 아카이빙 및 삭제
            activitySubjectPostJooqRepository.archiveAllByPostUlids(publishedPostUlids);
            activitySubjectPostJooqRepository.deleteAllAndRelatedRecordsByMemberIdAndPostUlids(memberId, publishedPostUlids);
        }
        List<String> likedPostIds =
                activitySubjectPostJooqRepository.getPostUlidsLikedByMemberId(memberId);
        activitySubjectPostJooqRepository.decreaseLikeCountByPostUlids(likedPostIds);     // 게시글 좋아요 감소
        List<ActivitySubjectCommentIdRecord> likedCommentIds =
                activitySubjectCommentJooqRepository.getCommentIdsLikedByMemberId(memberId);
        activitySubjectCommentJooqRepository.decreaseLikeCountByCommentIds(likedCommentIds);        // 댓글 좋아요 감소
        postLikeJpaRepository.deleteByMemberId(memberId);
        commentLikeJpaRepository.deleteByMemberId(memberId);

        String memberProfileImageFileKey = memberProfileJpaRepository.findByUuid(memberId).orElseThrow().getImagePath();
        if (memberProfileImageFileKey != null) {
            eventPublisher.publishEvent(ImageRemoveTask.create(memberProfileImageFileKey));
        }
        memberWithdrawJpaRepository.save(
                MemberWithdrawEntity.builder()
                        .member(memberJpaRepository.findByUuid(memberId).orElseThrow())
                        .reason(reason)
                        .opinion(opinion)
                        .build());
        activitySubjectCommentJooqRepository.markAsWithdrawnByMemberId(memberId);
        memberJooqRepository.updateMemberIdToNullByMemberId(memberId);        // 회원 식별자 정리
        memberJooqRepository.deleteMemberRelatedRecordsExceptOfPostAndCommentByMemberId(memberId); // 남은 회원 데이터 제거
    }
}
