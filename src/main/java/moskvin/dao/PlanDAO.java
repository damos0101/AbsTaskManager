package moskvin.dao;

import moskvin.models.Person;
import moskvin.models.Plan;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlanDAO {
    private final JdbcTemplate jdbcTemplate;

    public PlanDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Plan plan) {
        jdbcTemplate.update("insert into plan(person_id, name, start_date, end_date) values(?,?,?,?)",
                plan.getPerson_id(), plan.getName(), plan.getStart_date(), plan.getEnd_date());
    }

    public List<Plan> getPlansByPersonId(int id) {
        return jdbcTemplate.query("select * from plan where person_id = ?",
                new Object[]{id}, new BeanPropertyRowMapper<>(Plan.class));
    }

    public Plan show(int id) {
        return jdbcTemplate.query("select * from plan where id=?",
                        new Object[]{id}, new BeanPropertyRowMapper<>(Plan.class))
                .stream().findAny().orElse(null);
    }
}
