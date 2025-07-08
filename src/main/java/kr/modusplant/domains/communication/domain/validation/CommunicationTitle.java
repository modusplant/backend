package kr.modusplant.domains.communication.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank(message = "게시글 제목이 비어 있습니다.")
@Length(max = 150, message = "게시글 제목은 최대 150글자까지 작성할 수 있습니다.")
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommunicationTitle {
    String message() default "제목에서 오류가 발생했습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
