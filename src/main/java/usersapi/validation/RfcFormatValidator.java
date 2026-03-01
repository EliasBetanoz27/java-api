package usersapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class RfcFormatValidator implements ConstraintValidator<RfcFormat, String> {

    private static final Pattern RFC_PATTERN = Pattern.compile("^[A-Z&Ñ]{4}[0-9]{6}[A-Z0-9]{3}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        // Remove spaces and convert to uppercase for validation
        String cleaned = value.replaceAll("\\s+", "").toUpperCase();
        
        return RFC_PATTERN.matcher(cleaned).matches();
    }
}
