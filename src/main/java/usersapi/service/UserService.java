package usersapi.service;

import usersapi.model.User;
import usersapi.repository.UserRepository;
import usersapi.util.AESUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    // =============================
    // GET USERS (sorted + filter)
    // =============================
    public List<User> getUsers(String sortedBy, String filter) {

        List<User> users = new ArrayList<>(repository.findAll());

        // FILTER
        if (filter != null && !filter.isBlank()) {
            users = applyFilter(users, filter);
        }

        // SORT
        if (sortedBy != null && !sortedBy.isBlank()) {
            users = applySort(users, sortedBy);
        }

        return users;
    }

    // =============================
    // CREATE USER
    // =============================
    public Map<String, Object> createUser(User user) {

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }

        repository.findByTaxId(user.getTaxId())
                .ifPresent(u -> {
                    throw new RuntimeException("taxId already exists");
                });

        user.setId(UUID.randomUUID());
        user.setPassword(AESUtil.encrypt(user.getPassword()));
        repository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "new user created");
        response.put("user", user);
        return response;
    }

    // =============================
    // DELETE USER
    // =============================
    public void deleteUser(UUID id) {

        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        repository.delete(user);
    }

    // =============================
    // LOGIN
    // =============================
    public User login(String taxId, String password) {

        User user = repository.findByTaxId(taxId)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        String decryptedPassword = AESUtil.decrypt(user.getPassword());
        if (!decryptedPassword.equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    // =============================
    // FILTER LOGIC
    // =============================
    private List<User> applyFilter(List<User> users, String filter) {

        String[] parts = filter.replace(" ", "+").split("\\+");

        if (parts.length != 3) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Filter must follow format: field+operator+value");
        }

        String field = parts[0];
        String operator = parts[1];
        String value = parts[2];

        return users.stream()
                .filter(user -> match(user, field, operator, value))
                .collect(Collectors.toList());
    }

    private boolean match(User user, String field, String operator, String value) {

        String fieldValue = switch (field) {
            case "email" -> user.getEmail();
            case "id" -> user.getId().toString();
            case "name" -> user.getName();
            case "phone" -> user.getPhone();
            case "tax_id" -> user.getTaxId();
            case "created_at" -> user.getCreatedAt().toString();
            default -> "";
        };

        return switch (operator) {
            case "co" -> fieldValue.contains(value);
            case "sw" -> fieldValue.startsWith(value);
            case "ew" -> fieldValue.endsWith(value);
            case "eq" -> fieldValue.equals(value);
            default -> false;
        };
    }

    // =============================
    // SORT LOGIC
    // =============================
    private List<User> applySort(List<User> users, String sortedBy) {

        Comparator<User> comparator = switch (sortedBy) {
            case "email" -> Comparator.comparing(User::getEmail);
            case "id" -> Comparator.comparing(User::getId);
            case "name" -> Comparator.comparing(User::getName);
            case "phone" -> Comparator.comparing(User::getPhone);
            case "tax_id" -> Comparator.comparing(User::getTaxId);
            case "created_at" -> Comparator.comparing(User::getCreatedAt);
            default -> null;
        };

        if (comparator != null) {
            users.sort(comparator);
        }

        return users;
    }
}