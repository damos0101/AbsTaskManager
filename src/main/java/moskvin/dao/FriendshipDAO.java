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
        return jdbcTemplate.query("SELECT p.id, p.username, p.name\n" +
                        "FROM person p\n" +
                        "INNER JOIN friendship f1 ON p.id = f1.person2_id\n" +
                        "INNER JOIN friendship f2 ON p.id = f2.person1_id\n" +
                        "WHERE f1.person1_id = ? AND f2.person2_id = ?",
                new Object[]{id, id}, new BeanPropertyRowMapper<>(Person.class));
    }

    /*public List<Person> getFriendshipRequestsByPersonId(Integer userId) {
    }*/

    public void sendFriendshipRequest(int person1_id, int person2_id){
        jdbcTemplate.update("INSERT INTO friendship (person1_id, person2_id) VALUES (?, ?)", person1_id, person2_id);
    }

    public boolean areFriends(Integer userId, int friendId) {
        String query = "SELECT COUNT(*) FROM friendship WHERE (person1_id = ? AND person2_id = ?) OR (person1_id = ? AND person2_id = ?)";
        int count = jdbcTemplate.queryForObject(query, new Object[]{userId, friendId, friendId, userId}, Integer.class);
        return count > 1;
    }

    public boolean hasPendingRequest(Integer userId, int friendId) {
        String query = "SELECT COUNT(*) FROM friendship WHERE person1_id = ? AND person2_id = ?";
        int count = jdbcTemplate.queryForObject(query, new Object[]{userId, friendId}, Integer.class);
        return count > 0;
    }


    public List<Person> getSentFriendshipRequestsByPersonId(Integer id) {
        return jdbcTemplate.query("SELECT p.id, p.username, p.name\n" +
                        "FROM person p\n" +
                        "         INNER JOIN friendship f ON p.id = f.person2_id\n" +
                        "WHERE f.person1_id = ?\n" +
                        "  AND f.person2_id NOT IN (SELECT person1_id FROM friendship WHERE person2_id = ?)",
                new Object[]{id, id}, new BeanPropertyRowMapper<>(Person.class));
    }

    public List<Person> getReceivedFriendshipRequestsByPersonId(Integer id) {
        return jdbcTemplate.query("SELECT p.id, p.username, p.name\n" +
                "FROM person p\n" +
                "         INNER JOIN friendship f ON p.id = f.person1_id\n" +
                "WHERE f.person2_id = ?\n" +
                "  AND f.person1_id NOT IN (SELECT person2_id FROM friendship WHERE person1_id = ?)",
                new Object[]{id, id}, new BeanPropertyRowMapper<>(Person.class));
    }

    public void cancelFriendshipRequest(Integer userId, int friendId) {
        jdbcTemplate.update("delete from friendship where person1_id = ? and person2_id=?", userId, friendId);
    }
}
