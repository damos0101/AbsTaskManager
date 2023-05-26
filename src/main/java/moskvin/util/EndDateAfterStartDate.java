package moskvin.util;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EndDateAfterStartDateValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
public @interface EndDateAfterStartDate {
    String message() default "Дата закінчення повинна бути пізнішою за дату початку";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
