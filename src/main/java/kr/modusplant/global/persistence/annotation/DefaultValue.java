package kr.modusplant.global.persistence.annotation;

import java.lang.annotation.*;

/**
 * Marker annotation that indicates that the annotated field has the default value set on the table.
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface DefaultValue {
}