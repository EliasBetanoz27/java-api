package usersapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import usersapi.validation.AndresFormat;
import usersapi.model.Address;
import lombok.Data;
import java.util.List;

@Data
public class UserRequestDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @AndresFormat
    private String phone;

    @NotBlank
    private String password;

    @NotBlank
    private String taxId;
    
    private List<Address> addresses;
}
