package foodie_backend.foodie_backend.repository;

import foodie_backend.foodie_backend.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemRepository {

    private static final String INSERT =
            "INSERT INTO order_items (order_id, menu_item_id, item_name, unit_price, quantity) "
                    + "VALUES (?, ?, ?, ?, ?)";

    private static final RowMapper<OrderItem> ROW_MAPPER = (resultSet, rowNumber) -> {
        OrderItem item = new OrderItem();
        item.setId(resultSet.getLong("id"));
        item.setOrderId(resultSet.getLong("order_id"));
        item.setMenuItemId(resultSet.getLong("menu_item_id"));
        item.setItemName(resultSet.getString("item_name"));
        item.setUnitPrice(resultSet.getInt("unit_price"));
        item.setQuantity(resultSet.getInt("quantity"));
        return item;
    };

    private final JdbcTemplate jdbcTemplate;

    public OrderItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(long orderId, List<OrderItem> items) {
        for (OrderItem item : items) {
            jdbcTemplate.update(INSERT, orderId, item.getMenuItemId(), item.getItemName(),
                    item.getUnitPrice(), item.getQuantity());
        }
    }

    public List<OrderItem> findByOrderId(Long orderId) {
        return jdbcTemplate.query(
                "SELECT id, order_id, menu_item_id, item_name, unit_price, quantity "
                        + "FROM order_items WHERE order_id = ? ORDER BY id",
                ROW_MAPPER, orderId);
    }
}
