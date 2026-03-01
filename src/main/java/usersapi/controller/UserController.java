package usersapi.controller;

import usersapi.model.User;
import usersapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    // =============================
    // GET /users
    // =============================
    @Operation(summary = "Get all users with optional sorting and filtering")
    @GetMapping
    public List<User> getUsers(
            @Parameter(description = """
                    Sort users by one of the following fields:

                    - email
                    - id
                    - name
                    - phone
                    - tax_id
                    - created_at
                    """, examples = {
                    @ExampleObject(name = "Sort by name", value = "name"),
                    @ExampleObject(name = "Sort by email", value = "email"),
                    @ExampleObject(name = "Sort by creation date", value = "created_at"),
                    @ExampleObject(name = "Sort by tax id", value = "tax_id")
            })
            @RequestParam(required = false) String sortedBy,
            @Parameter(description = """
                    Dynamic filter using format: field+operator+value

                    Operators:
                    co  = contains
                    sw  = starts with
                    ew  = ends with
                    eq  = equals

                    Examples:
                    name+co+user
                    email+ew+mail.com
                    phone+sw+555
                    tax_id+eq+AARR990101XXX
                    """, examples = {
                    @ExampleObject(name = "Contains example", value = "name+co+user"),
                    @ExampleObject(name = "Ends with example", value = "email+ew+mail.com"),
                    @ExampleObject(name = "Starts with example", value = "phone+sw+555"),
                    @ExampleObject(name = "Equals example", value = "tax_id+eq+AARR990101XXX")
            }) @RequestParam(required = false) String filter) {
        return service.getUsers(sortedBy, filter);
    }

    // =============================
    // POST /users
    // =============================
    @PostMapping
    public ResponseEntity<?> createUser(@Parameter(description = "User object to be created") @RequestBody User user) {
        try {
            Map<String, Object> result = service.createUser(user);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // =============================
    // DELETE /users/{id}
    // =============================
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        service.deleteUser(id);
    }

    // =============================
    // POST /users/login
    // =============================
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String taxId,
            @RequestParam String password) {
        try {
            User user = service.login(taxId, password);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}