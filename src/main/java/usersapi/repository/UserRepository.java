package usersapi.repository;

import usersapi.model.Address;
import usersapi.model.User;
import usersapi.util.AESUtil;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Repository
public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public UserRepository() {
        loadInitialUsers();
    }

    private void loadInitialUsers() {

        ZoneId madagascarZone = ZoneId.of("Indian/Antananarivo");
        LocalDateTime now = LocalDateTime.now(madagascarZone);

        List<Address> addresses = List.of(
                new Address(1, "workaddress", "street No. 1", "UK"),
                new Address(2, "homeaddress", "street No. 2", "AU")
        );

        users.add(User.builder()
                .id(UUID.randomUUID())
                .email("user1@mail.com")
                .name("user1")
                .phone("+1 55 555 555 55")
                .password(AESUtil.encrypt("7c4a8d09ca3762af61e59520943dc26494f8941b"))
                .taxId("AARR990101XXX")
               .createdAt(now.plusHours(2))
                .addresses(addresses)
                .build());

        users.add(User.builder()
                .id(UUID.randomUUID())
                .email("user2@mail.com")
                .name("user2")
                .phone("+1 55 555 555 56")
                .password(AESUtil.encrypt("7c4a8d09ca3762af61e59520943dc26494f8941c"))
                .taxId("AARR990101XXY")
                .createdAt(now.plusHours(1))
                .addresses(addresses)
                .build());

        users.add(User.builder()
                .id(UUID.randomUUID())
                .email("user3@mail.com")
                .name("user3")
                .phone("+1 55 555 555 57")
                .password(AESUtil.encrypt("7c4a8d09ca3762af61e59520943dc26494f8941d"))
                .taxId("AARR990101XXZ")
                .createdAt(now.plusHours(3))
                .addresses(addresses)
                .build());
    }

    public List<User> findAll() {
        return users;
    }

    public Optional<User> findById(UUID id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public Optional<User> findByTaxId(String taxId) {
        return users.stream()
                .filter(user -> user.getTaxId().equalsIgnoreCase(taxId))
                .findFirst();
    }

    public void save(User user) {
        users.add(user);
    }

    public void delete(User user) {
        users.remove(user);
    }
}