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
import kr.modusplant.shared.framework.aws.event.ImageRemoveTask;
import kr.modusplant.shared.framework.aws.event.ImagesRemoveTask;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.kernel.Nickname;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
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

    /**
     * 회원 탈퇴 워크플로우
     * <ol>
     *     <li>Amazon S3: 회원의 모든 게시글(발행·임시저장)의 이미지와 식물 정보 사진 이미지 삭제</li>
     *     <li>Amazon S3: 회원 프로필 이미지 삭제</li>
     *     <li>Redis: 다른 유저의 최근에 본 게시글 데이터에서 발행된 게시글을 제거하고, 해당 게시글의 조회수 데이터 삭제</li>
     *     <li>Redis: 회원의 최근에 본 게시글 데이터와 검색 기록 삭제</li>
     *     <li>회원이 발신자 또는 수신자이거나, 발행된 게시글·해당 게시글에 포함된 댓글·회원의 모든 댓글을 대상으로 하는 알림 삭제</li>
     *     <li>발행된 게시글에 대한 좋아요·북마크·신고·신고 대시보드 삭제 후, 발행된 게시글 아카이빙</li>
     *     <li>발행된 게시글에 포함된 댓글과 회원의 모든 댓글에 대한 좋아요·신고·신고 대시보드 삭제 후, 해당 댓글 아카이빙</li>
     *     <li>발행된 게시글에 포함된 댓글과 회원의 모든 댓글 삭제 후, 회원의 모든 게시글(발행·임시저장) 삭제</li>
     *     <li>회원이 좋아요한 게시글·댓글의 좋아요 수 감소</li>
     *     <li>탈퇴 사유·의견 저장</li>
     *     <li>게시글·댓글·건의 및 버그 제보 아카이브와 건의 및 버그 제보에서 회원 식별자를 null로 변경</li>
     *     <li>refresh_token, comm_post_like, comm_comment_like, comm_post_bookmark, comm_post_abu_rep,
     *         comm_comment_abu_rep, site_member_prof, site_member_term, site_member_auth, site_member,
     *         fcm_token, plant_space, plant_info 에서 회원 데이터 삭제(plant_info_photo는 FK의 ON DELETE CASCADE로 삭제)</li>
     * </ol>
     * Amazon S3·Redis 작업(1~4)은 이벤트로 발행되어 트랜잭션 커밋 후에 수행된다.
     */
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
        if (!fileKeysToDelete.isEmpty()) {
            eventPublisher.publishEvent(ImagesRemoveTask.create(fileKeysToDelete));
        }
        String memberProfileImageFileKey = memberJooqRepository.getProfileImagePathByMemberId(memberId);
        if (memberProfileImageFileKey != null) {
            eventPublisher.publishEvent(ImageRemoveTask.create(memberProfileImageFileKey));
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
