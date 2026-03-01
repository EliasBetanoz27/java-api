package usersapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AndresFormatValidator implements ConstraintValidator<AndresFormat, String> {

    private static final String PHONE_REGEX =
            "^(\\+\\d{1,3})?\\d{10}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return false;
        }

        // Remove spaces before validation
        String cleaned = value.replaceAll("\\s+", "");

        return cleaned.matches(PHONE_REGEX);
    }
}