package usersapi.controller;

import usersapi.model.User;
import usersapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;

    // =============================
    // POST /login
    // =============================
    @Operation(summary = "Authenticate user with tax_id and password")
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "Tax ID of the user", required = true)
            @RequestParam String taxId,
            @Parameter(description = "Password of the user", required = true)
            @RequestParam String password) {
        try {
            User user = service.login(taxId, password);
            
            // Remove password from response for security
            user.setPassword(null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
