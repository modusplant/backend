package kr.modusplant.domains.member.framework.outbound;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.enums.MemberWithdrawReason;
import kr.modusplant.domains.member.domain.event.MemberSearchHistoryRemoveEvent;
import kr.modusplant.domains.member.domain.event.PostViewCountRemoveEvent;
import kr.modusplant.domains.member.domain.event.RecentlyViewMemberRemoveEvent;
import kr.modusplant.domains.member.domain.event.RecentlyViewPostRemoveEvent;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberWithdrawOpinion;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ActivitySubjectCommentJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.ActivitySubjectPostJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jooq.repository.MemberJooqRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberWithdrawEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.mapper.supers.MemberJpaMapper;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberWithdrawJpaRepository;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.domains.post.framework.outbound.jooq.record.PostIsPublishedAndContentRecord;
import kr.modusplant.domains.post.framework.outbound.jooq.repository.PostJooqRepository;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.framework.aws.event.ImagesRemoveTask;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.kernel.Nickname;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_MEMBER_ID;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepository {
    private final ApplicationEventPublisher eventPublisher;

    private final MemberJpaMapper memberJpaMapper;
    private final MemberJpaRepository memberJpaRepository;

    private final MemberWithdrawJpaRepository memberWithdrawJpaRepository;

    private final ActivitySubjectPostJooqRepository activitySubjectPostJooqRepository;
    private final ActivitySubjectCommentJooqRepository activitySubjectCommentJooqRepository;
    private final MemberJooqRepository memberJooqRepository;
    private final PostJooqRepository postJooqRepository;

    @Override
    public Member getById(MemberId memberId) {
        Optional<MemberEntity> emptyOrMemberEntity = memberJpaRepository.findByUuid(memberId.getValue());
        if (emptyOrMemberEntity.isPresent()) {
            return memberJpaMapper.toMember(emptyOrMemberEntity.orElseThrow());
        } else {
            throw new NotFoundEntityException(NOT_FOUND_MEMBER_ID, "memberId");
        }
    }

    @Override
    public Role getRole(MemberId memberId) {
        return memberJpaRepository.findByUuid(memberId.getValue())
                .orElseThrow(() -> new NotFoundEntityException(NOT_FOUND_MEMBER_ID, "memberId"))
                .getRole();
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

        List<PostIsPublishedAndContentRecord> posts = postJooqRepository.getPostIsPublishedAndContentsByMemberId(memberId);
        String[] publishedPostUlids = posts.stream()
                .filter(PostIsPublishedAndContentRecord::isPublished)
                .map(PostIsPublishedAndContentRecord::ulid)
                .toArray(String[]::new);

        List<String> fileKeysToDelete = new ArrayList<>();
        for (PostIsPublishedAndContentRecord post : posts) {
            JsonNode content = post.content();
            if (content == null || !content.isArray()) {
                continue;
            }
            for (JsonNode node : content) {
                if (node.has("src")) {
                    fileKeysToDelete.add(node.get("src").asText());
                }
            }
        }
        fileKeysToDelete.addAll(memberJooqRepository.getPlantInfoPhotoPathsByMemberId(memberId));
        String memberProfileImageFileKey = memberJooqRepository.getProfileImagePathByMemberId(memberId);
        if (!StringUtils.isBlank(memberProfileImageFileKey)) {
            fileKeysToDelete.add(memberProfileImageFileKey);
        }
        if (!fileKeysToDelete.isEmpty()) {
            eventPublisher.publishEvent(ImagesRemoveTask.create(fileKeysToDelete));
        }

        if (!ArrayUtils.isEmpty(publishedPostUlids)) {
            eventPublisher.publishEvent(RecentlyViewPostRemoveEvent.create(publishedPostUlids));
            eventPublisher.publishEvent(PostViewCountRemoveEvent.create(publishedPostUlids));
        }
        eventPublisher.publishEvent(RecentlyViewMemberRemoveEvent.create(memberId));
        eventPublisher.publishEvent(MemberSearchHistoryRemoveEvent.create(memberId));

        // 게시글·댓글 삭제 전에 수행되어야 하는 작업(하위 쿼리로 게시글·댓글 참조)
        memberJooqRepository.deleteNotificationsByMemberIdAndPostUlids(memberId, publishedPostUlids);
        if (publishedPostUlids.length != 0) {
            activitySubjectPostJooqRepository.deleteRelatedRecordsByPostUlids(publishedPostUlids);
            activitySubjectPostJooqRepository.archiveAllByPostUlids(publishedPostUlids);
        }
        activitySubjectCommentJooqRepository.deleteRelatedRecordsByMemberIdAndPostUlids(memberId, publishedPostUlids);
        activitySubjectCommentJooqRepository.archiveAllByMemberIdAndPostUlids(memberId, publishedPostUlids);

        activitySubjectCommentJooqRepository.deleteAllByMemberIdAndPostUlids(memberId, publishedPostUlids);
        if (!posts.isEmpty()) {
            activitySubjectPostJooqRepository.deleteAllByMemberId(memberId);
        }

        activitySubjectPostJooqRepository.decreaseLikeCountOfPostsLikedByMemberId(memberId);
        activitySubjectCommentJooqRepository.decreaseLikeCountOfCommentsLikedByMemberId(memberId);

        memberWithdrawJpaRepository.save(
                MemberWithdrawEntity.builder()
                        .member(memberJpaRepository.getReferenceById(memberId))
                        .reason(reason)
                        .opinion(opinion)
                        .build());
        memberJooqRepository.updateMemberIdToNullByMemberId(memberId);
        memberJooqRepository.deleteMemberRelatedRecordsExceptOfPostAndCommentByMemberId(memberId);
    }
}
