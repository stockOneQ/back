package umc.stockoneqback.file.utils.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = S3DirValidator.class)
public @interface ValidateDir {
    String message() default "올바르지 않은 경로입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
