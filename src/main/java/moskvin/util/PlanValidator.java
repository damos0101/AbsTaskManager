package moskvin.util;

import moskvin.dao.PlanDAO;
import moskvin.models.Plan;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PlanValidator implements Validator {
    private final PlanDAO planDAO;

    public PlanValidator(PlanDAO planDAO) {
        this.planDAO = planDAO;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Plan.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Plan plan = (Plan) o;
        if(plan.getStart_date() != null && plan.getEnd_date() != null && plan.getEnd_date().before(plan.getStart_date())){
            errors.rejectValue("end_date", "", "Дата закінчення повинна бути пізнішою за дату початку");
        }
    }
}
