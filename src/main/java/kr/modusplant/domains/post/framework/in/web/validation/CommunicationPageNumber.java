package kr.modusplant.domains.post.framework.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotNull(message = "페이지 숫자가 비어 있습니다.")
@Min(value = 1, message = "페이지 숫자는 1보다 크거나 같아야 합니다.")
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommunicationPageNumber {
    String message() default "페이지 숫자에서 오류가 발생했습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}