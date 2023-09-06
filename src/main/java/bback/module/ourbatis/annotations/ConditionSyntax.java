package bback.module.ourbatis.annotations;

import bback.module.ourbatis.enums.SqlConditionSyntax;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionSyntax {
    SqlConditionSyntax syntax() default SqlConditionSyntax.EQ;
}
