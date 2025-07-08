package kr.modusplant.domains.communication.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank(message = "경로가 비어 있습니다.")
@Pattern(regexp = "^\\d+(?:\\.\\d+)*$", message = "경로 서식이 올바르지 않습니다.")
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommunicationPath {
    String message() default "경로에서 오류가 발생했습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
