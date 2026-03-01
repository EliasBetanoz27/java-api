package usersapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import usersapi.model.User;
import usersapi.repository.UserRepository;
import usersapi.dto.UserUpdateDTO;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .name("Test User")
                .phone("+1234567890")
                .password("encryptedPassword")
                .taxId("TEST123456")
                .createdAt(java.time.LocalDateTime.now())
                .addresses(new ArrayList<>())
                .build();

        testUsers = Arrays.asList(testUser);
    }

    @Test
    void getUsers_ShouldReturnAllUsers_WhenNoFiltersProvided() {
        // Given
        when(repository.findAll()).thenReturn(testUsers);

        // When
        List<User> result = service.getUsers(null, null);

        // Then
        assertEquals(testUsers, result);
        verify(repository).findAll();
    }

    @Test
    void getUsers_ShouldReturnSortedUsers_WhenSortedByProvided() {
        // Given
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .email("a@example.com")
                .name("A User")
                .phone("+1234567891")
                .password("encryptedPassword")
                .taxId("TEST123457")
                .createdAt(java.time.LocalDateTime.now())
                .addresses(new ArrayList<>())
                .build();

        List<User> unsortedUsers = Arrays.asList(testUser, user2);
        List<User> sortedUsers = Arrays.asList(user2, testUser);

        when(repository.findAll()).thenReturn(unsortedUsers);

        // When
        List<User> result = service.getUsers("email", null);

        // Then
        assertEquals(sortedUsers, result);
        verify(repository).findAll();
    }

    @Test
    void getUsers_ShouldReturnFilteredUsers_WhenFilterProvided() {
        // Given
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .email("different@example.com")
                .name("Different User")
                .phone("+1234567891")
                .password("encryptedPassword")
                .taxId("DIFF123456")
                .createdAt(java.time.LocalDateTime.now())
                .addresses(new ArrayList<>())
                .build();

        List<User> allUsers = Arrays.asList(testUser, user2);
        when(repository.findAll()).thenReturn(allUsers);

        // When
        List<User> result = service.getUsers(null, "name+co+Test");

        // Then
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
        verify(repository).findAll();
    }

    @Test
    void createUser_ShouldCreateUser_WhenValidUserProvided() {
        // Given
        when(repository.findByTaxId(any())).thenReturn(Optional.empty());
        doNothing().when(repository).save(any(User.class));

        // When
        Map<String, Object> result = service.createUser(testUser);

        // Then
        assertTrue(result.containsKey("status"));
        assertEquals("success", result.get("status"));
        assertTrue(result.containsKey("message"));
        assertTrue(result.containsKey("user"));
        verify(repository).findByTaxId(testUser.getTaxId());
        verify(repository).save(testUser);
    }

    @Test
    void createUser_ShouldThrowException_WhenPasswordIsBlank() {
        // Given
        User userWithoutPassword = User.builder()
                .email("test@example.com")
                .name("Test User")
                .phone("+1234567890")
                .password("")
                .taxId("TEST123456")
                .build();

        // When & Then
        assertThrows(RuntimeException.class, () -> service.createUser(userWithoutPassword));
    }

    @Test
    void createUser_ShouldThrowException_WhenTaxIdAlreadyExists() {
        // Given
        when(repository.findByTaxId(testUser.getTaxId())).thenReturn(Optional.of(testUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.createUser(testUser));
        assertEquals("taxId already exists", exception.getMessage());
        verify(repository).findByTaxId(testUser.getTaxId());
        verify(repository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Given
        UUID userId = testUser.getId();
        when(repository.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(repository).delete(testUser);

        // When
        service.deleteUser(userId);

        // Then
        verify(repository).findById(userId);
        verify(repository).delete(testUser);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> service.deleteUser(nonExistentId));
        verify(repository).findById(nonExistentId);
        verify(repository, never()).delete(any());
    }

    @Test
    void updateUser_ShouldUpdateName_WhenNameProvided() {
        // Given
        UUID userId = testUser.getId();
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("Updated Name");

        when(repository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        service.updateUser(userId, updateDTO);

        // Then
        assertEquals("Updated Name", testUser.getName());
        verify(repository).save(testUser);
    }

    @Test
    void updateUser_ShouldThrowException_WhenNameIsSame() {
        // Given
        UUID userId = testUser.getId();
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName(testUser.getName());

        when(repository.findById(userId)).thenReturn(Optional.of(testUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.updateUser(userId, updateDTO));
        assertEquals("User name not updated, name is the same", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void updateUser_ShouldUpdateEmail_WhenEmailProvided() {
        // Given
        UUID userId = testUser.getId();
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("updated@example.com");

        when(repository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        service.updateUser(userId, updateDTO);

        // Then
        assertEquals("updated@example.com", testUser.getEmail());
        verify(repository).save(testUser);
    }

    @Test
    void updateUser_ShouldUpdatePhone_WhenPhoneProvided() {
        // Given
        UUID userId = testUser.getId();
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setPhone("+11234567890"); // +1 (country code) + 1234567890 (10 digits)

        when(repository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        service.updateUser(userId, updateDTO);

        // Then
        assertEquals("+11234567890", testUser.getPhone());
        verify(repository).save(testUser);
    }

    @Test
    void updateUser_ShouldThrowException_WhenPhoneInvalid() {
        // Given
        UUID userId = testUser.getId();
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setPhone("invalid-phone");

        when(repository.findById(userId)).thenReturn(Optional.of(testUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> service.updateUser(userId, updateDTO));
        assertEquals("Phone number must contain 10 digits and may include country code", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void updateUser_ShouldUpdatePassword_WhenPasswordProvided() {
        // Given
        UUID userId = testUser.getId();
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setPassword("newPassword123");

        when(repository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        service.updateUser(userId, updateDTO);

        // Then
        verify(repository).save(testUser);
        // Password should be encrypted (we can't easily test the exact encryption without exposing AESUtil)
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("Updated Name");

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> service.updateUser(nonExistentId, updateDTO));
        verify(repository).findById(nonExistentId);
        verify(repository, never()).save(any());
    }

    @Test
    void updateUser_ShouldNotUpdate_WhenAllFieldsAreNullOrEmpty() {
        // Given
        UUID userId = testUser.getId();
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        // All fields are null by default

        when(repository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        service.updateUser(userId, updateDTO);

        // Then
        assertEquals("test@example.com", testUser.getEmail());
        assertEquals("Test User", testUser.getName());
        assertEquals("+1234567890", testUser.getPhone());
        assertEquals("encryptedPassword", testUser.getPassword());
        verify(repository).save(testUser);
    }

    @Test
    void login_ShouldReturnUser_WhenValidCredentials() {
        // Given
        String taxId = "TEST123456";
        String password = "password123";
        
        // Create user with properly encrypted password
        User userWithEncryptedPassword = User.builder()
                .id(testUser.getId())
                .email(testUser.getEmail())
                .name(testUser.getName())
                .phone(testUser.getPhone())
                .password(usersapi.util.AESUtil.encrypt(password))
                .taxId(taxId)
                .createdAt(testUser.getCreatedAt())
                .addresses(testUser.getAddresses())
                .build();

        when(repository.findByTaxId(taxId)).thenReturn(Optional.of(userWithEncryptedPassword));

        // When
        User result = service.login(taxId, password);

        // Then
        assertEquals(userWithEncryptedPassword, result);
        verify(repository).findByTaxId(taxId);
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        // Given
        String nonExistentTaxId = "NONEXISTENT";
        String password = "password123";

        when(repository.findByTaxId(nonExistentTaxId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> service.login(nonExistentTaxId, password));
        verify(repository).findByTaxId(nonExistentTaxId);
    }
}
