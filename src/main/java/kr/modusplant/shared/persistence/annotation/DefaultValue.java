package kr.modusplant.shared.persistence.annotation;

import java.lang.annotation.*;

/**
 * 수식된 필드는 Persist 시에 App 수준에서 디폴트 값을 줄 수 있어야 한다는 것을 의미하는 마커 어노테이션입니다.
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface DefaultValue {
}