package moskvin.util;

import moskvin.dao.TaskDAO;
import moskvin.models.Plan;
import moskvin.models.Task;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class TaskValidator implements Validator {
    private final TaskDAO taskDAO;

    public TaskValidator(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Task.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Task task = (Task) o;

    }
}
