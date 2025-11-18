package kr.modusplant.framework.jpa.generator;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD,FIELD})
@IdGeneratorType(UlidIdGenerator.class)
public @interface UlidGenerator {
}
