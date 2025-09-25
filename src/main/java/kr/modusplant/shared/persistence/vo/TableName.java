package kr.modusplant.shared.persistence.vo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TableName {
    public static final String COMM_COMMENT = "comm_comment";
    public static final String COMM_POST = "comm_post";
    public static final String COMM_POST_LIKE = "comm_post_like";
    public static final String COMM_PRI_CATE = "comm_pri_cate";
    public static final String COMM_SECO_CATE = "comm_seco_cate";
    public static final String SITE_MEMBER = "site_member";
    public static final String SITE_MEMBER_AUTH = "site_member_auth";
    public static final String SITE_MEMBER_ROLE = "site_member_role";
    public static final String SITE_MEMBER_TERM = "site_member_term";
    public static final String TERM = "term";
}