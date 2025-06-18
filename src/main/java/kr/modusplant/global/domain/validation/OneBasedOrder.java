package kr.modusplant.global.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotNull(message = "순서가 비어 있습니다.")
@Range(min = 1, max = 100, message = "순서는 0부터 100 사이의 값이어야 합니다.")
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface OneBasedOrder {
    String message() default "순서에서 오류가 발생했습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
