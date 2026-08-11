package kr.modusplant.domains.search.domain.vo.nullobject;

import kr.modusplant.domains.search.domain.vo.SearcherId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptySearcherId extends SearcherId {
    private static final EmptySearcherId instance = new EmptySearcherId();

    public static EmptySearcherId create() {
        return instance;
    }

    @Override
    public UUID getValue() {
        return null;
    }
}
