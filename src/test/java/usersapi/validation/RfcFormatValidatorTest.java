package usersapi.validation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RfcFormatValidatorTest {

    private final RfcFormatValidator validator = new RfcFormatValidator();

    @Test
    void isValid_ShouldReturnTrue_WhenValidRfcProvided() {
        assertTrue(validator.isValid("AARR990101XXX", null));
        assertTrue(validator.isValid("BOMM850101YYY", null));
    }

    @Test
    void isValid_ShouldReturnFalse_WhenInvalidRfcProvided() {
        assertFalse(validator.isValid("123", null));
        assertFalse(validator.isValid("RFC", null));
        assertFalse(validator.isValid("AARR99XXX", null));
    }

    @Test
    void isValid_ShouldReturnFalse_WhenNullOrEmpty() {
        assertFalse(validator.isValid(null, null));
        assertFalse(validator.isValid("", null));
    }
}
