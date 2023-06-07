package moskvin.dao;

import moskvin.models.Person;
import moskvin.models.Task;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OptimizationDAO {
    private final JdbcTemplate jdbcTemplate;

    public OptimizationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addPersonToPlanOptimization(int planId, int personId){
        jdbcTemplate.update("insert into plan_person(plan_id, person_id) values (?,?)", planId, personId);
    }

    public void addPersonToTask(int taskId, int personId){
        jdbcTemplate.update("insert into task_person(task_id, person_id) values (?,?)", taskId, personId);
    }

    public void deleteOptimization(int planId){
        jdbcTemplate.update("DELETE FROM task_person WHERE task_id IN (SELECT id FROM Task WHERE plan_id = ?)", planId);
    }

    public Person getPersonByTaskId(int taskId){
        return jdbcTemplate.query("select p.* from task_person tp join person p on tp.person_id=p.id where tp.task_id=?",
                        new Object[]{taskId}, new BeanPropertyRowMapper<>(Person.class))
                .stream().findAny().orElse(null);
    }

    public void deleteFromTaskPersonByPersonAndPlanId(int personId, int planId){
        jdbcTemplate.update("delete from task_person where task_id in(select id FROM task WHERE plan_id = ?) AND person_id = ?",
                planId, personId);
        jdbcTemplate.update("delete from plan_person where plan_id = ? and person_id = ?", planId, personId);
    }

    public List<Person> getPeopleForOptimization(int planId){
        return jdbcTemplate.query("SELECT p.* FROM plan_person pp JOIN person p ON pp.person_id = p.id WHERE pp.plan_id = ?",
                new Object[]{planId}, new BeanPropertyRowMapper<>(Person.class));
    }

    public boolean isPersonAlreadyInPlan(int planId, int personId){
        String sql = "SELECT COUNT(*) FROM plan_person WHERE person_id = ? AND plan_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[]{personId, planId}, Integer.class);
        return count > 0;
    }
}
