package moskvin.dao;

import moskvin.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersonDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Person person){
        jdbcTemplate.update("insert into person(name, date, email, username, password) values(?,?,?,?,?)",
                person.getName(), person.getDate(), person.getEmail(), person.getUsername(), person.getPassword());
    }

    public Person show(int id) {
        return jdbcTemplate.query("select * from person where id=?",
                        new Object[]{id}, new BeanPropertyRowMapper<>(Person.class))
                .stream().findAny().orElse(null);
    }

    public Person findById(int id){
        return jdbcTemplate.query("select * from person where id=?",
                new Object[]{id}, new BeanPropertyRowMapper<>(Person.class)).stream().findAny().orElse(null);
    }

    public Person findByUsername(String username){
        return jdbcTemplate.query("select * from person where username=?",
                new Object[]{username}, new BeanPropertyRowMapper<>(Person.class)).stream().findAny().orElse(null);
    }

    public Optional<Person> showByUsername(String username){
        return jdbcTemplate.query("select * from person where username=?",
                        new Object[]{username}, new BeanPropertyRowMapper<>(Person.class))
                .stream().findAny();
    }

    public Optional<Person> showByEmail(String email){
        return jdbcTemplate.query("select * from person where email=?",
                        new Object[]{email}, new BeanPropertyRowMapper<>(Person.class))
                .stream().findAny();
    }

    public void updateData(int id, Person updatedPerson) {
        jdbcTemplate.update("update person set name=?, date=?, password=? where id=?",
                updatedPerson.getName(), updatedPerson.getDate(), updatedPerson.getPassword(), id);
    }
}
