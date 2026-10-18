package foodie_backend.foodie_backend.repository;

import foodie_backend.foodie_backend.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.util.List;

@Repository
public class ReservationRepository {

    private static final String INSERT =
            "INSERT INTO reservations (customer_name, phone, reservation_date, reservation_time, guests) "
                    + "VALUES (?, ?, ?, ?, ?)";

    private static final RowMapper<Reservation> ROW_MAPPER = (resultSet, rowNumber) -> {
        Reservation reservation = new Reservation();
        reservation.setId(resultSet.getLong("id"));
        reservation.setCustomerName(resultSet.getString("customer_name"));
        reservation.setPhone(resultSet.getString("phone"));
        Date date = resultSet.getDate("reservation_date");
        if (date != null) {
            reservation.setReservationDate(date.toLocalDate());
        }
        Time time = resultSet.getTime("reservation_time");
        if (time != null) {
            reservation.setReservationTime(time.toLocalTime());
        }
        reservation.setGuests(resultSet.getInt("guests"));
        return reservation;
    };

    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Reservation reservation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, reservation.getCustomerName());
            statement.setString(2, reservation.getPhone());
            statement.setDate(3, Date.valueOf(reservation.getReservationDate()));
            statement.setTime(4, Time.valueOf(reservation.getReservationTime()));
            statement.setInt(5, reservation.getGuests());
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        long id = generatedId == null ? 0 : generatedId.longValue();
        reservation.setId(id);
        return id;
    }

    public List<Reservation> findAll() {
        return jdbcTemplate.query(
                "SELECT id, customer_name, phone, reservation_date, reservation_time, guests "
                        + "FROM reservations ORDER BY reservation_date DESC, reservation_time DESC",
                ROW_MAPPER);
    }

    public int count() {
        Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reservations", Integer.class);
        return total == null ? 0 : total;
    }
}
