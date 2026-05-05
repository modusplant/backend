package kr.modusplant.infrastructure.event.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.framework.jooq.converter.JsonbJsonNodeConverter;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.ImageRemoveEvent;
import kr.modusplant.shared.event.MemberWithdrawalEvent;
import kr.modusplant.shared.event.RecentlyViewPostRemoveEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Component
public class MemberEventConsumer {
    private final StringRedisTemplate stringRedisTemplate;
    private final DSLContext dsl;
    private final ApplicationEventPublisher eventPublisher;
    private final JsonbJsonNodeConverter jsonbJsonNodeConverter = new JsonbJsonNodeConverter();

    public MemberEventConsumer(EventBus eventBus,
                               StringRedisTemplate stringRedisTemplate,
                               DSLContext dsl,
                               ApplicationEventPublisher eventPublisher) {
        eventBus.subscribe(event -> {
            if (event instanceof MemberWithdrawalEvent memberWithdrawalEvent) {
                deleteAllWithMemberPKAndAlterAllWithMemberFK(
                        memberWithdrawalEvent.getMemberId(),
                        memberWithdrawalEvent.getReason(),
                        memberWithdrawalEvent.getOpinion()
                );
            }
        });
        this.stringRedisTemplate = stringRedisTemplate;
        this.dsl = dsl;
        this.eventPublisher = eventPublisher;
    }

    private void deleteAllWithMemberPKAndAlterAllWithMemberFK(UUID memberId, String reason, String opinion) {
        stringRedisTemplate.unlink("recentlyView:member:%s:posts".formatted(memberId));     // 최근에 본 게시글 데이터 삭제

        String[] publishedPostUlids = dsl.select(COMM_POST.ULID)    // 발행되어 타 회원이 접근할 수 있는 게시글 ID 획득
                .from(COMM_POST)
                .where(COMM_POST.AUTH_MEMB_UUID.eq(memberId))
                .and(COMM_POST.IS_PUBLISHED.isTrue())
                .fetchInto(String.class).toArray(new String[0]);

        List<JsonNode> publishedPostContents = dsl.select(COMM_POST.CONTENT)    // 발행된 게시글의 컨텐츠 획득
                .from(COMM_POST)
                .where(COMM_POST.ULID.in(publishedPostUlids))
                .fetchInto(JSONB.class)
                .stream()
                .map(jsonbJsonNodeConverter::from)
                .toList();

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
            eventPublisher.publishEvent(ImageRemoveEvent.create(fileKeysToDelete));
        }
        if (!ArrayUtils.isEmpty(publishedPostUlids)) {
            eventPublisher.publishEvent(RecentlyViewPostRemoveEvent.create(publishedPostUlids));
        }
        processPostsAndRelatedRecords(memberId, publishedPostUlids);
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

                    dsl.deleteFrom(COMM_POST)
                            .where(COMM_POST.AUTH_MEMB_UUID.eq(memberId))
            ).execute();
        }
    }

    private void processOtherMemberRelatedRecords(UUID memberId, String reason, String opinion) {
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
                        .where(COMM_COMMENT.AUTH_MEMB_UUID.eq(memberId)),

                dsl.update(COMM_POST_ARCHIVE)
                        .setNull(COMM_POST_ARCHIVE.AUTH_MEMB_UUID)
                        .set(COMM_POST_ARCHIVE.UPDATED_AT, LocalDateTime.now())
                        .where(COMM_POST_ARCHIVE.AUTH_MEMB_UUID.eq(memberId)),

                dsl.update(PROP_BUG_REP)
                        .setNull(PROP_BUG_REP.MEMB_UUID)
                        .set(PROP_BUG_REP.LAST_MODIFIED_AT, LocalDateTime.now())
                        .set(PROP_BUG_REP.VER_NUM, coalesce(PROP_BUG_REP.VER_NUM, 0).plus(1))
                        .where(PROP_BUG_REP.MEMB_UUID.eq(memberId)),

                dsl.update(PROP_BUG_REP_ARCHIVE)
                        .setNull(PROP_BUG_REP_ARCHIVE.MEMB_UUID)
                        .set(PROP_BUG_REP_ARCHIVE.LAST_MODIFIED_AT, LocalDateTime.now())
                        .where(PROP_BUG_REP_ARCHIVE.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(REFRESH_TOKEN)
                        .where(REFRESH_TOKEN.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_POST_LIKE)
                        .where(COMM_POST_LIKE.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_POST_BOOKMARK)
                        .where(COMM_POST_BOOKMARK.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_POST_ABU_REP)
                        .where(COMM_POST_ABU_REP.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_COMMENT_LIKE)
                        .where(COMM_COMMENT_LIKE.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_COMMENT_ABU_REP)
                        .where(COMM_COMMENT_ABU_REP.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER_PROF)
                        .where(SITE_MEMBER_PROF.UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER_TERM)
                        .where(SITE_MEMBER_TERM.UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER_AUTH)
                        .where(SITE_MEMBER_AUTH.UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER)
                        .where(SITE_MEMBER.UUID.eq(memberId))

        ).execute();
    }
}
