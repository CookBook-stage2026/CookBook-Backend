package cookbook.stage.backend.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NoDuplicateDaysValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoDuplicateDays {
    String message() default "Duplicate days are not allowed in the schedule";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
