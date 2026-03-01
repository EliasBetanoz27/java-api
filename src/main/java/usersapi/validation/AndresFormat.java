package usersapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AndresFormatValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AndresFormat {

    String message() default "Phone number must contain 10 digits and may include country code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}