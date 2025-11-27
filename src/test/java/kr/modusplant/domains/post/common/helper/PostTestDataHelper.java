package kr.modusplant.domains.post.common.helper;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.infrastructure.converter.JsonNodeConverter;
import kr.modusplant.jooq.tables.records.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.generator.EventType;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.*;

@Component
@RequiredArgsConstructor
public class PostTestDataHelper {
    private static final UlidIdGenerator generator = new UlidIdGenerator();
    private final DSLContext dsl;

    public SiteMemberRecord insertTestMember(String nickname) {
        LocalDateTime dateTime = LocalDateTime.now().minusMonths(3);
        return dsl.insertInto(SITE_MEMBER)
                .set(SITE_MEMBER.UUID, UUID.randomUUID())
                .set(SITE_MEMBER.NICKNAME, nickname)
                .set(SITE_MEMBER.IS_ACTIVE, true)
                .set(SITE_MEMBER.IS_DISABLED_BY_LINKING, true)
                .set(SITE_MEMBER.IS_BANNED, true)
                .set(SITE_MEMBER.IS_DELETED, true)
                .set(SITE_MEMBER.CREATED_AT, dateTime)
                .set(SITE_MEMBER.LAST_MODIFIED_AT, dateTime)
                .set(SITE_MEMBER.VER_NUM, 1)
                .returning()
                .fetchOneInto(SiteMemberRecord.class);
    }

    public CommPriCateRecord insertTestPrimaryCategory(String category, int order) {
        return dsl.insertInto(COMM_PRI_CATE)
                .set(COMM_PRI_CATE.UUID,UUID.randomUUID())
                .set(COMM_PRI_CATE.CATEGORY,category)
                .set(COMM_PRI_CATE.ORDER,order)
                .set(COMM_PRI_CATE.CREATED_AT,LocalDateTime.now().minusYears(3))
                .returning()
                .fetchOneInto(CommPriCateRecord.class);
    }

    public CommSecoCateRecord insertTestSecondaryCategory(CommPriCateRecord priCateRecord, String category, int order) {
        return dsl.insertInto(COMM_SECO_CATE)
                .set(COMM_SECO_CATE.UUID,UUID.randomUUID())
                .set(COMM_SECO_CATE.CATEGORY,category)
                .set(COMM_SECO_CATE.ORDER,order)
                .set(COMM_SECO_CATE.CREATED_AT,LocalDateTime.now().minusYears(3))
                .set(COMM_SECO_CATE.PRI_CATE_UUID,priCateRecord.getUuid())
                .returning()
                .fetchOneInto(CommSecoCateRecord.class);
    }

    public CommPostRecord insertTestPublishedPost(
            CommPriCateRecord priCateRecord, CommSecoCateRecord secoCateRecord,
            SiteMemberRecord memberRecord, String title, JsonNode content
    ) {
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(10);
        return dsl.insertInto(COMM_POST)
                .set(COMM_POST.ULID,generator.generate(null,null,null, EventType.INSERT))
                .set(COMM_POST.PRI_CATE_UUID,priCateRecord.getUuid())
                .set(COMM_POST.SECO_CATE_UUID,secoCateRecord.getUuid())
                .set(COMM_POST.AUTH_MEMB_UUID,memberRecord.getUuid())
                .set(COMM_POST.CREA_MEMB_UUID,memberRecord.getUuid())
                .set(COMM_POST.LIKE_COUNT,30)
                .set(COMM_POST.VIEW_COUNT,251)
                .set(COMM_POST.TITLE,title)
                .set(COMM_POST.CONTENT,new JsonNodeConverter().to(content))
                .set(COMM_POST.IS_PUBLISHED,true)
                .set(COMM_POST.PUBLISHED_AT,dateTime)
                .set(COMM_POST.CREATED_AT,dateTime)
                .set(COMM_POST.UPDATED_AT,dateTime)
                .set(COMM_POST.VER,1)
                .returning()
                .fetchOneInto(CommPostRecord.class);
    }

    public CommPostRecord insertTestDraftPost(
            CommPriCateRecord priCateRecord, CommSecoCateRecord secoCateRecord,
            SiteMemberRecord memberRecord, String title, JsonNode content
    ) {
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(10);
        return dsl.insertInto(COMM_POST)
                .set(COMM_POST.ULID,generator.generate(null,null,null, EventType.INSERT))
                .set(COMM_POST.PRI_CATE_UUID,priCateRecord.getUuid())
                .set(COMM_POST.SECO_CATE_UUID,secoCateRecord.getUuid())
                .set(COMM_POST.AUTH_MEMB_UUID,memberRecord.getUuid())
                .set(COMM_POST.CREA_MEMB_UUID,memberRecord.getUuid())
                .set(COMM_POST.LIKE_COUNT,0)
                .set(COMM_POST.VIEW_COUNT,0)
                .set(COMM_POST.TITLE,title)
                .set(COMM_POST.CONTENT,new JsonNodeConverter().to(content))
                .set(COMM_POST.IS_PUBLISHED,false)
                .set(COMM_POST.CREATED_AT,dateTime)
                .set(COMM_POST.UPDATED_AT,dateTime)
                .set(COMM_POST.VER,1)
                .returning()
                .fetchOneInto(CommPostRecord.class);
    }

    public CommCommentRecord insertTestComment(CommPostRecord postRecord, String path, SiteMemberRecord memberRecord, String content, boolean isDeleted) {
        return dsl.insertInto(COMM_COMMENT)
                .set(COMM_COMMENT.POST_ULID,postRecord.getUlid())
                .set(COMM_COMMENT.PATH,path)
                .set(COMM_COMMENT.AUTH_MEMB_UUID,memberRecord.getUuid())
                .set(COMM_COMMENT.CREA_MEMB_UUID,memberRecord.getUuid())
                .set(COMM_COMMENT.CONTENT,content)
                .set(COMM_COMMENT.LIKE_COUNT,2)
                .set(COMM_COMMENT.IS_DELETED,isDeleted)
                .set(COMM_COMMENT.CREATED_AT,LocalDateTime.now().minusMinutes(1))
                .returning()
                .fetchOneInto(CommCommentRecord.class);
    }

    public CommPostLikeRecord insertTestPostLike(CommPostRecord postRecord, SiteMemberRecord memberRecord) {
        return dsl.insertInto(COMM_POST_LIKE)
                .set(COMM_POST_LIKE.POST_ULID,postRecord.getUlid())
                .set(COMM_POST_LIKE.MEMB_UUID,memberRecord.getUuid())
                .set(COMM_POST_LIKE.CREATED_AT,LocalDateTime.now())
                .returning()
                .fetchOneInto(CommPostLikeRecord.class);
    }

    public CommPostBookmarkRecord insertTestPostBookmark(CommPostRecord postRecord, SiteMemberRecord memberRecord) {
        return dsl.insertInto(COMM_POST_BOOKMARK)
                .set(COMM_POST_BOOKMARK.POST_ULID,postRecord.getUlid())
                .set(COMM_POST_BOOKMARK.MEMB_UUID,memberRecord.getUuid())
                .set(COMM_POST_BOOKMARK.CREATED_AT,LocalDateTime.now())
                .returning()
                .fetchOneInto(CommPostBookmarkRecord.class);
    }

    public void deleteTestPostWithRelations(CommPostRecord... posts) {
        String[] ulids = Arrays.stream(posts)
                .map(CommPostRecord::getUlid)
                .toArray(String[]::new);

        // 댓글 삭제
        dsl.deleteFrom(COMM_COMMENT)
                .where(COMM_COMMENT.POST_ULID.in(ulids))
                .execute();

        // 좋아요 삭제
        dsl.deleteFrom(COMM_POST_LIKE)
                .where(COMM_POST_LIKE.POST_ULID.in(ulids))
                .execute();

        // 북마크 삭제
        dsl.deleteFrom(COMM_POST_BOOKMARK)
                .where(COMM_POST_BOOKMARK.POST_ULID.in(ulids))
                .execute();

        // 게시글 삭제
        dsl.deleteFrom(COMM_POST)
                .where(COMM_POST.ULID.in(ulids))
                .execute();
    }

    public void deleteTestCategory(CommPriCateRecord... primaryCategories) {
        UUID[] uuids = Arrays.stream(primaryCategories)
                .map(CommPriCateRecord::getUuid)
                .toArray(UUID[]::new);

        // 2차 카테고리 삭제
        dsl.deleteFrom(COMM_SECO_CATE)
                .where(COMM_SECO_CATE.PRI_CATE_UUID.in(uuids))
                .execute();

        // 1차 카테고리 삭제
        dsl.deleteFrom(COMM_PRI_CATE)
                .where(COMM_PRI_CATE.UUID.in(uuids))
                .execute();
    }

    public void deleteTestMember(SiteMemberRecord... members) {
        UUID[] uuids = Arrays.stream(members)
                .map(SiteMemberRecord::getUuid)
                .toArray(UUID[]::new);

        dsl.deleteFrom(SITE_MEMBER)
                .where(SITE_MEMBER.UUID.in(uuids))
                .execute();
    }

}
