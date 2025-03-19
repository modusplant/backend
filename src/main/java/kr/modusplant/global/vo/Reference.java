package kr.modusplant.global.vo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Reference {
    public static final String NOTATION_ENTITY = "kr.modusplant.global.persistence.entity";
    public static final String NOTATION_REPOSITORY = "kr.modusplant.global.persistence.repository";
    public static final String NOTATION_SERVICE_IMPL = "kr.modusplant.global.persistence.service";
}
