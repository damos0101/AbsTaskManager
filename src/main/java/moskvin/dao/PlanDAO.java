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

    public Person getPersonByPlanId(int id){
        return jdbcTemplate.query("SELECT person.* FROM person JOIN plan ON person.id = plan.person_id WHERE plan.id = ?",
                new Object[]{id}, new BeanPropertyRowMapper<>(Person.class)).stream().findAny().orElse(null);
    }

    public Plan findById(int id) {
        return jdbcTemplate.query("select * from plan where id=?",
                        new Object[]{id}, new BeanPropertyRowMapper<>(Plan.class))
                .stream().findAny().orElse(null);
    }

    public void addAccess(int personId, int planId){
        jdbcTemplate.update("insert into plan_access(person_id, plan_id) VALUES (?,?)", personId, planId);
    }

    public List<Person> getUsersByPlanAccess(int planId){
        return jdbcTemplate.query("SELECT person.* FROM person JOIN plan_access ON person.id = plan_access.person_id WHERE plan_access.plan_id = ?",
                new Object[]{planId}, new BeanPropertyRowMapper<>(Person.class));
    }

    public List<Plan> getPlansAccessByPersonId(Integer userId) {
        return jdbcTemplate.query("SELECT plan.* FROM plan JOIN plan_access ON plan.id = plan_access.plan_id WHERE plan_access.person_id = ?",
                new Object[]{userId}, new BeanPropertyRowMapper<>(Plan.class));
    }

    public boolean isHaveAccess(int userId, int planId){
        String sql = "SELECT COUNT(*) FROM plan_access WHERE person_id = ? AND plan_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[]{userId, planId}, Integer.class);
        return count > 0;
    }

    public void deletePlan(int planId) {
        jdbcTemplate.update("delete from plan where id=?", planId);
    }

    public void deleteAccess(int planId){
        jdbcTemplate.update("delete from plan_access where plan_id=?", planId);
    }

    public void takeawayAccess(int planId, int personId){
        jdbcTemplate.update("delete from plan_access where plan_id=? and person_id=?", planId, personId);
    }
}
