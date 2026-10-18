package foodie_backend.foodie_backend.repository;

import foodie_backend.foodie_backend.model.Order;
import foodie_backend.foodie_backend.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    private static final String COLUMNS =
            "id, user_id, customer_name, phone, address, payment_method, delivery_charge, total_amount, status, order_date";

    private static final String INSERT =
            "INSERT INTO orders (user_id, customer_name, phone, address, payment_method, delivery_charge, total_amount, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final RowMapper<Order> ROW_MAPPER = (resultSet, rowNumber) -> {
        Order order = new Order();
        order.setId(resultSet.getLong("id"));
        long userId = resultSet.getLong("user_id");
        order.setUserId(resultSet.wasNull() ? null : userId);
        order.setCustomerName(resultSet.getString("customer_name"));
        order.setPhone(resultSet.getString("phone"));
        order.setAddress(resultSet.getString("address"));
        order.setPaymentMethod(resultSet.getString("payment_method"));
        order.setDeliveryCharge(resultSet.getInt("delivery_charge"));
        order.setTotalAmount(resultSet.getInt("total_amount"));
        order.setStatus(resultSet.getString("status"));
        Timestamp orderDate = resultSet.getTimestamp("order_date");
        if (orderDate != null) {
            order.setOrderDate(orderDate.toLocalDateTime());
        }
        return order;
    };

    private final JdbcTemplate jdbcTemplate;
    private final OrderItemRepository orderItemRepository;

    public OrderRepository(JdbcTemplate jdbcTemplate, OrderItemRepository orderItemRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.orderItemRepository = orderItemRepository;
    }

    public long save(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT, new String[]{"id"});
            if (order.getUserId() == null) {
                statement.setNull(1, java.sql.Types.BIGINT);
            } else {
                statement.setLong(1, order.getUserId());
            }
            statement.setString(2, order.getCustomerName());
            statement.setString(3, order.getPhone());
            statement.setString(4, order.getAddress());
            statement.setString(5, order.getPaymentMethod());
            statement.setInt(6, order.getDeliveryCharge());
            statement.setInt(7, order.getTotalAmount());
            statement.setString(8, order.getStatus());
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        long orderId = generatedId == null ? 0 : generatedId.longValue();
        order.setId(orderId);
        return orderId;
    }

    public List<Order> findAll() {
        return jdbcTemplate.query("SELECT " + COLUMNS + " FROM orders ORDER BY id DESC", ROW_MAPPER);
    }

    public List<Order> findByUserId(Long userId) {
        return jdbcTemplate.query("SELECT " + COLUMNS + " FROM orders WHERE user_id = ? ORDER BY id DESC",
                ROW_MAPPER, userId);
    }

    public Optional<Order> findById(Long id) {
        List<Order> orders = jdbcTemplate.query("SELECT " + COLUMNS + " FROM orders WHERE id = ?", ROW_MAPPER, id);
        return orders.stream().findFirst();
    }

    public void saveItems(long orderId, List<OrderItem> items) {
        orderItemRepository.save(orderId, items);
    }

    public List<OrderItem> findItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public void updateStatus(Long id, String status) {
        jdbcTemplate.update("UPDATE orders SET status = ? WHERE id = ?", status, id);
    }

    public int count() {
        Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orders", Integer.class);
        return total == null ? 0 : total;
    }
}
