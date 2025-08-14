package kr.modusplant.shared.database;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TableColumnName {
    public static final String PRI_CATE_UUID = "pri_cate_uuid";
    public static final String SECO_CATE_UUID = "seco_cate_uuid";
    public static final String CREATED_AT = "created_at";
    public static final String IS_DELETED = "is_deleted";
    public static final String LAST_MODIFIED_AT = "last_modified_at";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String UPDATED_AT = "updated_at";
    public static final String VER_NUM = "ver_num";
}
