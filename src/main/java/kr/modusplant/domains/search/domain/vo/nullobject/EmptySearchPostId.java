package kr.modusplant.domains.search.domain.vo.nullobject;

import kr.modusplant.domains.search.domain.vo.SearchPostId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptySearchPostId extends SearchPostId {
    private static final EmptySearchPostId instance = new EmptySearchPostId();

    public static EmptySearchPostId create() {
        return instance;
    }

    @Override
    public String getValue() {
        return null;
    }
}
