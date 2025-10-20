package kr.modusplant.legacy.domains.communication.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotNull(message = "Order must not be null.")
@Range(min = 0, max = 100, message = "Order must be range from 0 to 100.")
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommunicationOrder {
    String message() default "order error occurred";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
