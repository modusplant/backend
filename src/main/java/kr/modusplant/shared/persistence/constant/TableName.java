package kr.modusplant.shared.persistence.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TableName {
    public static final String COMM_COMMENT = "comm_comment";
    public static final String COMM_COMMENT_LIKE = "comm_comment_like";
    public static final String COMM_POST = "comm_post";
    public static final String COMM_POST_ARCHIVE = "comm_post_archive";
    public static final String COMM_POST_BOOKMARK = "comm_post_bookmark";
    public static final String COMM_POST_LIKE = "comm_post_like";
    public static final String COMM_PRI_CATE = "comm_pri_cate";
    public static final String COMM_SECO_CATE = "comm_seco_cate";
    public static final String PROP_BUG_REP = "prop_bug_rep";
    public static final String SITE_MEMBER = "site_member";
    public static final String SITE_MEMBER_AUTH = "site_member_auth";
    public static final String SITE_MEMBER_PROF = "site_member_prof";
    public static final String SITE_MEMBER_ROLE = "site_member_role";
    public static final String SITE_MEMBER_TERM = "site_member_term";
    public static final String SWEAR = "swear";
    public static final String TERM = "term";
}