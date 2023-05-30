package moskvin.dao;

import moskvin.models.Plan;
import moskvin.models.Task;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskDAO {
    private final JdbcTemplate jdbcTemplate;

    public TaskDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Task task) {
        jdbcTemplate.update("insert into task(plan_id, name, start_time, end_time, completed) values(?,?,?,?,?)",
                task.getPlan_id(), task.getName(), task.getStart_time(), task.getEnd_time(), false);
    }

    public List<Task> getTaskByPlanId(int id){
        return jdbcTemplate.query("select * from task where plan_id = ? order by end_time",
                new Object[]{id}, new BeanPropertyRowMapper<>(Task.class));
    }

    public Task show(int id) {
        return jdbcTemplate.query("select * from task where id=?",
                        new Object[]{id}, new BeanPropertyRowMapper<>(Task.class))
                .stream().findAny().orElse(null);
    }

    public void deleteTask(int id){
        jdbcTemplate.update("delete from task where id=?", id);
    }

    public void setCompleted(boolean completed, int taskId) {
        jdbcTemplate.update("UPDATE task SET completed = ? WHERE id = ?", completed, taskId);
    }
}
