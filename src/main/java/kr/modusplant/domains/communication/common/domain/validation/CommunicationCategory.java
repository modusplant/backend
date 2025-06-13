package kr.modusplant.domains.communication.common.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Length(max = 40, message = "Category must be at maximum 40 strings.")
@NotEmpty(message = "Category must not be empty.")
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommunicationCategory {
    String message() default "category error occurred";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
