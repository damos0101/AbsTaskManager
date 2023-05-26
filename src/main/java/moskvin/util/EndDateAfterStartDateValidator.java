package moskvin.util;

import moskvin.models.Plan;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

public class EndDateAfterStartDateValidator implements ConstraintValidator<EndDateAfterStartDate, Plan> {

    @Override
    public void initialize(EndDateAfterStartDate constraintAnnotation) {
        // Метод ініціалізації (не потребує додаткової логіки)
    }

    @Override
    public boolean isValid(Plan plan, ConstraintValidatorContext context) {
        if (plan == null || plan.getStart_date() == null || plan.getEnd_date() == null) {
            // Якщо одне з полів або сам об'єкт є нульовим, повертаємо true (валідацію пропускаємо)
            return true;
        }

        Date startDate = plan.getStart_date();
        Date endDate = plan.getEnd_date();

        return endDate.after(startDate);
    }
}
