package com.pedalbuildpipeline.pbp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AcceptableContentTypeValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AcceptableContentType {
  String message() default "contains unacceptable content types";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String[] acceptableContentTypes();
}
