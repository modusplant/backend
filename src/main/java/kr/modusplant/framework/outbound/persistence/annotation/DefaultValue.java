package kr.modusplant.framework.outbound.persistence.annotation;

import java.lang.annotation.*;

/**
 * 수식된 필드는 테이블의 행 수준에서 설정된 디폴트 값을 가진다는 것을 의미하는 마커 어노테이션입니다.
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface DefaultValue {
}