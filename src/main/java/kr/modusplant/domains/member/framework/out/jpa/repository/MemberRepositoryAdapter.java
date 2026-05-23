package kr.modusplant.domains.member.framework.out.jpa.repository;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.member.domain.aggregate.Member;
import kr.modusplant.domains.member.domain.enums.MemberWithdrawReason;
import kr.modusplant.domains.member.domain.vo.MemberId;
import kr.modusplant.domains.member.domain.vo.MemberWithdrawOpinion;
import kr.modusplant.domains.member.framework.out.jooq.record.ActivitySubjectCommentIdRecord;
import kr.modusplant.domains.member.framework.out.jooq.repository.ActivitySubjectCommentJooqRepository;
import kr.modusplant.domains.member.framework.out.jooq.repository.ActivitySubjectPostJooqRepository;
import kr.modusplant.domains.member.framework.out.jooq.repository.MemberProfileJooqRepository;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.mapper.MemberJpaMapperImpl;
import kr.modusplant.domains.member.usecase.port.repository.MemberRepository;
import kr.modusplant.domains.post.framework.out.jooq.repository.PostJooqRepository;
import kr.modusplant.shared.event.ImageRemoveEvent;
import kr.modusplant.shared.event.ImagesRemoveEvent;
import kr.modusplant.shared.event.RecentlyViewPostRemoveEvent;
import kr.modusplant.shared.framework.jpa.exception.NotFoundEntityException;
import kr.modusplant.shared.kernel.Nickname;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.DSLContext;
import org.jooq.Row2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.NOT_FOUND_MEMBER;
import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryAdapter implements MemberRepository {
    private final StringRedisTemplate stringRedisTemplate;
    private final DSLContext dsl;
    private final ApplicationEventPublisher eventPublisher;

    private final MemberJpaMapperImpl memberJpaMapper;
    private final MemberJpaRepository memberJpaRepository;

    private final ActivitySubjectPostJooqRepository activitySubjectPostJooqRepository;
    private final ActivitySubjectCommentJooqRepository activitySubjectCommentJooqRepository;
    private final MemberProfileJooqRepository memberProfileJooqRepository;
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
            eventPublisher.publishEvent(ImagesRemoveEvent.create(fileKeysToDelete));
        }
        if (!ArrayUtils.isEmpty(publishedPostUlids)) {
            eventPublisher.publishEvent(RecentlyViewPostRemoveEvent.create(publishedPostUlids));
        }
        processPostsAndRelatedRecords(memberId, publishedPostUlids);
        processPostAndCommentRecordsLikedByMember(memberId);
        processOtherMemberRelatedRecords(memberId, reason, opinion);
    }

    private void processPostsAndRelatedRecords(UUID memberId, String[] publishedPostUlids) {
        if (publishedPostUlids.length != 0) {
            dsl.batch(
                    dsl.insertInto(COMM_POST_ARCHIVE,
                                    COMM_POST_ARCHIVE.ULID,
                                    COMM_POST_ARCHIVE.PRI_CATE_ID,
                                    COMM_POST_ARCHIVE.SECO_CATE_ID,
                                    COMM_POST_ARCHIVE.AUTH_MEMB_UUID,
                                    COMM_POST_ARCHIVE.TITLE,
                                    COMM_POST_ARCHIVE.CONTENT_TEXT,
                                    COMM_POST_ARCHIVE.CREATED_AT,
                                    COMM_POST_ARCHIVE.ARCHIVED_AT,
                                    COMM_POST_ARCHIVE.UPDATED_AT,
                                    COMM_POST_ARCHIVE.PUBLISHED_AT
                            )
                            .select(
                                    select(
                                            COMM_POST.ULID,
                                            COMM_POST.PRI_CATE_ID,
                                            COMM_POST.SECO_CATE_ID,
                                            COMM_POST.AUTH_MEMB_UUID,
                                            COMM_POST.TITLE,
                                            COMM_POST.CONTENT_TEXT,
                                            COMM_POST.CREATED_AT,
                                            val(LocalDateTime.now()),
                                            COMM_POST.UPDATED_AT,
                                            COMM_POST.PUBLISHED_AT
                                    )
                                            .from(COMM_POST)
                                            .where(COMM_POST.ULID.in(publishedPostUlids))
                            ),

                    dsl.deleteFrom(COMM_POST_LIKE)
                            .where(COMM_POST_LIKE.POST_ULID.in(publishedPostUlids)),

                    dsl.deleteFrom(COMM_POST_BOOKMARK)
                            .where(COMM_POST_BOOKMARK.POST_ULID.in(publishedPostUlids)),

                    dsl.deleteFrom(COMM_COMMENT_LIKE)
                            .where(COMM_COMMENT_LIKE.POST_ULID.in(publishedPostUlids)),

                    dsl.deleteFrom(COMM_POST)
                            .where(COMM_POST.AUTH_MEMB_UUID.eq(memberId))
            ).execute();
        }
    }

    private void processPostAndCommentRecordsLikedByMember(UUID memberId) {
        List<String> activitySubjectPostIds = activitySubjectPostJooqRepository.getPostIdsLikedByMemberId(memberId);
        dsl.deleteFrom(COMM_POST_LIKE)
                .where(COMM_POST_LIKE.MEMB_UUID.eq(memberId)).execute();
        dsl.update(COMM_POST)
                .set(COMM_POST.LIKE_COUNT, COMM_POST.LIKE_COUNT.minus(1))
                .set(COMM_POST.UPDATED_AT, LocalDateTime.now())
                .set(COMM_POST.VER, coalesce(COMM_POST.VER, 0).plus(1))
                .where(
                        COMM_POST.ULID.in(activitySubjectPostIds)
                                .and(COMM_POST.LIKE_COUNT.gt(0)))   // 좋아요 수가 1 이상일 때만 좋아요 수 감소
                .execute();

        List<ActivitySubjectCommentIdRecord> activitySubjectCommentIds =
                activitySubjectCommentJooqRepository.getCommentIdsThatHaveCommentLikedByMemberId(memberId);
        dsl.deleteFrom(COMM_COMMENT_LIKE)
                .where(COMM_COMMENT_LIKE.MEMB_UUID.eq(memberId)).execute();
        List<Row2<String, String>> activitySubjectCommentIdRows = activitySubjectCommentIds.stream()
                .map(dto -> row(dto.postUlid(), dto.path()))
                .toList();
        dsl.update(COMM_COMMENT)
                .set(COMM_COMMENT.LIKE_COUNT, COMM_COMMENT.LIKE_COUNT.minus(1))
                .set(COMM_COMMENT.UPDATED_AT, LocalDateTime.now())
                .where(
                        row(COMM_COMMENT.POST_ULID, COMM_COMMENT.PATH).in(activitySubjectCommentIdRows)
                                .and(COMM_COMMENT.LIKE_COUNT.gt(0))   // 좋아요 수가 1 이상일 때만 좋아요 수 감소
                )
                .execute();
    }

    private void processOtherMemberRelatedRecords(UUID memberId, String reason, String opinion) {
        String memberProfileImageFileKey = memberProfileJooqRepository.getImageFileKeyFromMemberId(memberId);
        if (memberProfileImageFileKey != null) {
            eventPublisher.publishEvent(ImageRemoveEvent.create(memberProfileImageFileKey));
        }

        dsl.batch(
                dsl.insertInto(SITE_MEMBER_WITHDRAW,
                                SITE_MEMBER_WITHDRAW.UUID,
                                SITE_MEMBER_WITHDRAW.REASON,
                                SITE_MEMBER_WITHDRAW.OPINION,
                                SITE_MEMBER_WITHDRAW.WITHDRAWN_AT)
                        .values(
                                memberId,
                                reason,
                                opinion,
                                LocalDateTime.now()),

                dsl.update(COMM_COMMENT)
                        .setNull(COMM_COMMENT.AUTH_MEMB_UUID)
                        .set(COMM_COMMENT.IS_DELETED, true)
                        .set(COMM_COMMENT.UPDATED_AT, LocalDateTime.now())
                        .where(COMM_COMMENT.AUTH_MEMB_UUID.eq(memberId)),

                dsl.update(COMM_POST_ARCHIVE)
                        .setNull(COMM_POST_ARCHIVE.AUTH_MEMB_UUID)
                        .set(COMM_POST_ARCHIVE.UPDATED_AT, LocalDateTime.now())
                        .where(COMM_POST_ARCHIVE.AUTH_MEMB_UUID.eq(memberId)),

                dsl.update(PROP_BUG_REP)
                        .setNull(PROP_BUG_REP.MEMB_UUID)
                        .where(PROP_BUG_REP.MEMB_UUID.eq(memberId)),

                dsl.update(PROP_BUG_REP_ARCHIVE)
                        .setNull(PROP_BUG_REP_ARCHIVE.MEMB_UUID)
                        .where(PROP_BUG_REP_ARCHIVE.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(REFRESH_TOKEN)
                        .where(REFRESH_TOKEN.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_POST_BOOKMARK)
                        .where(COMM_POST_BOOKMARK.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_POST_ABU_REP)
                        .where(COMM_POST_ABU_REP.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_COMMENT_ABU_REP)
                        .where(COMM_COMMENT_ABU_REP.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER_PROF)
                        .where(SITE_MEMBER_PROF.UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER_TERM)
                        .where(SITE_MEMBER_TERM.UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER_AUTH)
                        .where(SITE_MEMBER_AUTH.UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER)
                        .where(SITE_MEMBER.UUID.eq(memberId)),

                dsl.deleteFrom(FCM_TOKEN)
                        .where(FCM_TOKEN.MEMB_UUID.eq(memberId))

        ).execute();
    }
}
