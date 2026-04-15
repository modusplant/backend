package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.MemberWithdrawalEvent;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.val;

@Component
public class MemberEventConsumer {
    private final StringRedisTemplate stringRedisTemplate;
    private final DSLContext dsl;

    public MemberEventConsumer(EventBus eventBus, StringRedisTemplate stringRedisTemplate, DSLContext dsl) {
        eventBus.subscribe(event -> {
            if (event instanceof MemberWithdrawalEvent memberWithdrawalEvent) {
                deleteAllWithMemberPKAndAlterAllWithMemberFK(memberWithdrawalEvent.getMemberId());
            }
        });
        this.stringRedisTemplate = stringRedisTemplate;
        this.dsl = dsl;
    }

    private void deleteAllWithMemberPKAndAlterAllWithMemberFK(UUID memberId) {
        stringRedisTemplate.delete("recentlyView:member:%s:posts".formatted(memberId));

        String[] deletedPublishedPostIds = deleteAllPostWithMemberPK(memberId);
        if (deletedPublishedPostIds.length != 0) {

            Set<String> targetKeys = new HashSet<>();

            stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
                ScanOptions options = ScanOptions.scanOptions()
                        .match("recentlyView:member:*:posts")
                        .count(1000)
                        .build();

                try (Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
                    while (cursor.hasNext()) {
                        targetKeys.add(new String(cursor.next()));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Redis SCAN failed", e);
                }
                return null;
            });

            if (!targetKeys.isEmpty()) {
                stringRedisTemplate.executePipelined(new SessionCallback<Object>() {
                    @Override
                    public <K, V> Object execute(@NotNull RedisOperations<K, V> operations) {
                        for (String key : targetKeys) {
                            ((StringRedisTemplate) operations).opsForZSet()
                                    .remove(key, (Object[]) deletedPublishedPostIds);
                        }
                        return null;
                    }
                });
            }
        }

        dsl.batch(
                dsl.update(COMM_COMMENT_ABU_REP)
                        .setNull(COMM_COMMENT_ABU_REP.MEMB_UUID)
                        .set(COMM_COMMENT_ABU_REP.LAST_MODIFIED_AT, LocalDateTime.now())
                        .where(COMM_COMMENT_ABU_REP.MEMB_UUID.eq(memberId)),

                dsl.update(COMM_COMMENT)
                        .setNull(COMM_COMMENT.AUTH_MEMB_UUID)
                        .set(COMM_COMMENT.IS_DELETED, true)
                        .where(COMM_COMMENT.AUTH_MEMB_UUID.eq(memberId)),

                dsl.update(COMM_POST_ABU_REP)
                        .setNull(COMM_POST_ABU_REP.MEMB_UUID)
                        .set(COMM_POST_ABU_REP.LAST_MODIFIED_AT, LocalDateTime.now())
                        .where(COMM_POST_ABU_REP.MEMB_UUID.eq(memberId)),

                dsl.update(COMM_POST_ARCHIVE)
                        .setNull(COMM_POST_ARCHIVE.AUTH_MEMB_UUID)
                        .set(COMM_POST_ARCHIVE.UPDATED_AT, LocalDateTime.now())
                        .where(COMM_POST_ARCHIVE.AUTH_MEMB_UUID.eq(memberId)),

                dsl.update(PROP_BUG_REP)
                        .setNull(PROP_BUG_REP.MEMB_UUID)
                        .set(PROP_BUG_REP.LAST_MODIFIED_AT, LocalDateTime.now())
                        .where(PROP_BUG_REP.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(REFRESH_TOKEN)
                        .where(REFRESH_TOKEN.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_POST_LIKE)
                        .where(COMM_POST_LIKE.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_POST_BOOKMARK)
                        .where(COMM_POST_BOOKMARK.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_COMMENT_LIKE)
                        .where(COMM_COMMENT_LIKE.MEMB_UUID.eq(memberId)),

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

    private String[] deleteAllPostWithMemberPK(UUID memberId) {
        List<String> targetUlids = dsl.select(COMM_POST.ULID)
                .from(COMM_POST)
                .where(COMM_POST.AUTH_MEMB_UUID.eq(memberId))
                .and(COMM_POST.IS_PUBLISHED.isTrue())
                .fetchInto(String.class);

        if (targetUlids.isEmpty()) {
            return new String[0];
        }

        dsl.batch(
                dsl.insertInto(COMM_POST_ARCHIVE,
                                COMM_POST_ARCHIVE.ULID,
                                COMM_POST_ARCHIVE.PRI_CATE_ID,
                                COMM_POST_ARCHIVE.SECO_CATE_ID,
                                COMM_POST_ARCHIVE.AUTH_MEMB_UUID,
                                COMM_POST_ARCHIVE.TITLE,
                                COMM_POST_ARCHIVE.CONTENT,
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
                                        COMM_POST.CONTENT,
                                        COMM_POST.CREATED_AT,
                                        val(LocalDateTime.now()),
                                        COMM_POST.UPDATED_AT,
                                        COMM_POST.PUBLISHED_AT
                                )
                                        .from(COMM_POST)
                                        .where(COMM_POST.ULID.in(targetUlids))
                        ),

                dsl.deleteFrom(COMM_POST_LIKE)
                        .where(COMM_POST_LIKE.POST_ULID.in(targetUlids)),

                dsl.deleteFrom(COMM_POST_BOOKMARK)
                        .where(COMM_POST_BOOKMARK.POST_ULID.in(targetUlids)),

                dsl.deleteFrom(COMM_POST)
                        .where(COMM_POST.AUTH_MEMB_UUID.eq(memberId))
        ).execute();

        return targetUlids.toArray(new String[0]);
    }
}
