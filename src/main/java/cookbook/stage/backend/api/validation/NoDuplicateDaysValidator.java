package cookbook.stage.backend.api.validation;

import be.xplore.cookbook.dto.request.CreateWeekScheduleDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.DayOfWeek;
import java.util.HashSet;

public class NoDuplicateDaysValidator implements ConstraintValidator<NoDuplicateDays, CreateWeekScheduleDto> {

    @Override
    public boolean isValid(CreateWeekScheduleDto dto, ConstraintValidatorContext context) {
        var uniqueDays = new HashSet<DayOfWeek>();

        for (var daySchedule : dto.days()) {
            if (daySchedule != null && !uniqueDays.add(daySchedule.day())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Duplicate day found: " + daySchedule.day()
                ).addPropertyNode("days").addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
