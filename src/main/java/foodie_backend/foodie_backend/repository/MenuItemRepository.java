package foodie_backend.foodie_backend.repository;

import foodie_backend.foodie_backend.model.MenuItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MenuItemRepository {

    private static final String SELECT_ALL =
            "SELECT id, name, category, price, food_type, image_url, description, available "
                    + "FROM menu_items ORDER BY id";

    private static final String SELECT_BY_ID =
            "SELECT id, name, category, price, food_type, image_url, description, available "
                    + "FROM menu_items WHERE id = ?";

    private static final String COUNT_ALL = "SELECT COUNT(*) FROM menu_items";

    private static final String INSERT =
            "INSERT INTO menu_items (name, category, price, food_type, image_url, description, available) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final RowMapper<MenuItem> ROW_MAPPER = (resultSet, rowNumber) -> new MenuItem(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("category"),
            resultSet.getInt("price"),
            resultSet.getString("food_type"),
            resultSet.getString("image_url"),
            resultSet.getString("description"),
            resultSet.getBoolean("available"));

    private final JdbcTemplate jdbcTemplate;

    public MenuItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MenuItem> findAll() {
        return jdbcTemplate.query(SELECT_ALL, ROW_MAPPER);
    }

    public Optional<MenuItem> findById(Long id) {
        List<MenuItem> items = jdbcTemplate.query(SELECT_BY_ID, ROW_MAPPER, id);
        return items.stream().findFirst();
    }

    public int count() {
        Integer total = jdbcTemplate.queryForObject(COUNT_ALL, Integer.class);
        return total == null ? 0 : total;
    }

    public void save(MenuItem item) {
        jdbcTemplate.update(INSERT,
                item.getName(),
                item.getCategory(),
                item.getPrice(),
                item.getFoodType(),
                item.getImageUrl(),
                item.getDescription(),
                item.isAvailable());
    }
}
