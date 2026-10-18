package foodie_backend.foodie_backend.repository;

import foodie_backend.foodie_backend.model.Admin;
import foodie_backend.foodie_backend.model.Customer;
import foodie_backend.foodie_backend.model.Person;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private static final String COLUMNS =
            "id, name, email, password_hash, role, auth_token, token_expires_at";

    private static final String INSERT =
            "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";

    private static final RowMapper<Person> ROW_MAPPER = (resultSet, rowNumber) -> {
        String role = resultSet.getString("role");
        Person person = "ADMIN".equals(role) ? new Admin() : new Customer();
        person.setId(resultSet.getLong("id"));
        person.setName(resultSet.getString("name"));
        person.setEmail(resultSet.getString("email"));
        person.setPasswordHash(resultSet.getString("password_hash"));
        person.setAuthToken(resultSet.getString("auth_token"));
        Timestamp expiresAt = resultSet.getTimestamp("token_expires_at");
        if (expiresAt != null) {
            person.setTokenExpiresAt(expiresAt.toLocalDateTime());
        }
        return person;
    };

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Person> findByEmail(String email) {
        List<Person> users = jdbcTemplate.query(
                "SELECT " + COLUMNS + " FROM users WHERE email = ?", ROW_MAPPER, email);
        return users.stream().findFirst();
    }

    public Optional<Person> findById(Long id) {
        List<Person> users = jdbcTemplate.query(
                "SELECT " + COLUMNS + " FROM users WHERE id = ?", ROW_MAPPER, id);
        return users.stream().findFirst();
    }

    public Optional<Person> findByValidToken(String token) {
        List<Person> users = jdbcTemplate.query(
                "SELECT " + COLUMNS + " FROM users WHERE auth_token = ? AND token_expires_at > ?",
                ROW_MAPPER, token, Timestamp.valueOf(LocalDateTime.now()));
        return users.stream().findFirst();
    }

    public long insert(Person person) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, person.getName());
            statement.setString(2, person.getEmail());
            statement.setString(3, person.getPasswordHash());
            statement.setString(4, person.getRole());
            return statement;
        }, keyHolder);
        Number generatedId = keyHolder.getKey();
        long id = generatedId == null ? 0 : generatedId.longValue();
        person.setId(id);
        return id;
    }

    public void updateToken(Long id, String token, LocalDateTime expiresAt) {
        jdbcTemplate.update("UPDATE users SET auth_token = ?, token_expires_at = ? WHERE id = ?",
                token, Timestamp.valueOf(expiresAt), id);
    }

    public void clearToken(Long id) {
        jdbcTemplate.update("UPDATE users SET auth_token = NULL, token_expires_at = NULL WHERE id = ?", id);
    }
}
