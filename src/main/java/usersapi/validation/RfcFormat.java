package usersapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RfcFormatValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RfcFormat {

    String message() default "Invalid RFC format. Expected format: XXXX000000XXX (e.g., AARR990101XXX)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
