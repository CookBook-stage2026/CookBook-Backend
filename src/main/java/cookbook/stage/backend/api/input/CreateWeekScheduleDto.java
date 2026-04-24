package cookbook.stage.backend.api.input;

import cookbook.stage.backend.api.validation.NoDuplicateDays;
import cookbook.stage.backend.api.validation.ValidationGroups;
import jakarta.validation.GroupSequence;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@NoDuplicateDays(groups = ValidationGroups.Second.class)
@GroupSequence({CreateWeekScheduleDto.class, ValidationGroups.First.class, ValidationGroups.Second.class})
public record CreateWeekScheduleDto(
        @Valid
        @NotNull(groups = ValidationGroups.First.class)
        List<CreateDayScheduleDto> days
) {
}
