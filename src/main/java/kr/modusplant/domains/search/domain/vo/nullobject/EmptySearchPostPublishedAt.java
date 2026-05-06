package kr.modusplant.domains.search.domain.vo.nullobject;

import kr.modusplant.domains.search.domain.vo.SearchPostPublishedAt;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptySearchPostPublishedAt extends SearchPostPublishedAt {
    public static EmptySearchPostPublishedAt create() {
        return instance;
    }
    private static final EmptySearchPostPublishedAt instance = new EmptySearchPostPublishedAt();

    @Override
    public LocalDateTime getValue() {
        return null;
    }
}
