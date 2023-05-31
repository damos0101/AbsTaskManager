package moskvin.dao;


import moskvin.models.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RoleDAO {
    private final JdbcTemplate jdbcTemplate;

    public RoleDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addRoleToUser(int personId, int planId, int roleId){
        jdbcTemplate.update("insert into user_role(person_id, plan_id, role_id) VALUES (?,?,?)",
                personId, planId, roleId);
    }

    public void updateRole(int personId, int planId, int roleId){
        jdbcTemplate.update("update user_role set role_id=? where person_id=? and plan_id=?", roleId, personId, planId);
    }

    public int getRoleByPersonIdAndPlanId(int personId, int planId){
        return jdbcTemplate.queryForObject("select role_id from user_role where person_id=? and plan_id=?", Integer.class, personId, planId);
    }

    public void deleteRole(int personId, int planId){
        jdbcTemplate.update("delete from user_role where person_id=? and plan_id=?", personId, planId);
    }
}
