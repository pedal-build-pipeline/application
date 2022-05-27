package com.pedalbuildpipeline.pbp.project.validation;

import com.pedalbuildpipeline.pbp.validation.AcceptableContentType;
import com.pedalbuildpipeline.pbp.validation.AcceptableFileSize;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.MediaType;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Length(max = 10)
@AcceptableFileSize(maximumSizeInBytes = 1024 * 1024 * 5)
@AcceptableContentType(
    acceptableContentTypes = {
      MediaType.APPLICATION_PDF_VALUE,
      MediaType.IMAGE_PNG_VALUE,
      MediaType.IMAGE_JPEG_VALUE
    })
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface ValidProjectMedia {
  String message() default "invalid project media";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
