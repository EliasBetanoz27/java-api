package usersapi.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateDTO {
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String name;
    
    private String phone;
    
    private String password;
}
