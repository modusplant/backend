package kr.modusplant.framework.jpa.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EntityName {

    COMM_COMMENT_ENTITY("commCommentEntity"),
    COMM_COMMENT_LIKE_ENTITY("commCommentLikeEntity"),
    COMM_POST_ARCHIVE_ENTITY("commPostArchiveEntity"),
    COMM_POST_BOOKMARK_ENTITY("commPostBookmarkEntity"),
    COMM_POST_ENTITY("commPostEntity"),
    COMM_POST_LIKE_ENTITY("commPostLikeEntity"),
    COMM_PRIMARY_CATEGORY_ENTITY("commPrimaryCategoryEntity"),
    COMM_SECONDARY_CATEGORY_ENTITY("commSecondaryCategoryEntity"),
    SITE_MEMBER_AUTH_ENTITY("siteMemberAuthEntity"),
    SITE_MEMBER_ENTITY("siteMemberEntity"),
    SITE_MEMBER_PROFILE_ENTITY("siteMemberProfileEntity"),
    SITE_MEMBER_ROLE_ENTITY("siteMemberRoleEntity"),
    SITE_MEMBER_TERM_ENTITY("siteMemberTermEntity"),
    TERM_ENTITY("termEntity"),
    ;

    private final String name;
}
