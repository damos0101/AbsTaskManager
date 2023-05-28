package moskvin.dao;

import moskvin.models.Person;
import moskvin.models.Plan;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FriendshipDAO {
    private final JdbcTemplate jdbcTemplate;

    public FriendshipDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> getFriendsByPersonId(int id) {
        return jdbcTemplate.query("SELECT p.id, p.name\n" +
                        "FROM person p\n" +
                        "         INNER JOIN friendship f ON p.id = f.person2_id\n" +
                        "WHERE f.person1_id = ?",
                new Object[]{id}, new BeanPropertyRowMapper<>(Person.class));
    }
}
