package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.weekschedule.DaySchedule;
import be.xplore.cookbook.core.domain.weekschedule.WeekSchedule;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record WeekScheduleInput(
        String weekRole,
        List<DayScheduleInput> schedule
) {
    public static WeekScheduleInput fromDomain(WeekSchedule weekSchedule) {
        return fromDomain("reference", weekSchedule, null);
    }

    public static WeekScheduleInput fromDomain(WeekSchedule weekSchedule, DayOfWeek dayToSuggestFor) {
        return fromDomain("current week", weekSchedule, dayToSuggestFor);
    }

    public record DayScheduleInput(
            DayOfWeek day,
            String recipeName,
            String recipeDescription
    ) {
        public static DayScheduleInput fromDomain(DaySchedule daySchedule) {
            return new DayScheduleInput(
                    daySchedule.day(),
                    daySchedule.recipe().name(),
                    daySchedule.recipe().description()
            );
        }
    }

    private static WeekScheduleInput fromDomain(String weekRole, WeekSchedule weekSchedule, DayOfWeek dayToSuggestFor) {
        Map<DayOfWeek, DaySchedule> scheduledDays = weekSchedule.dailyRecipes().stream()
                .collect(Collectors.toMap(DaySchedule::day, Function.identity()));

        List<DayScheduleInput> schedule = Arrays.stream(DayOfWeek.values())
                .map(day -> {
                    if (day == dayToSuggestFor) {
                        return new DayScheduleInput(day, null, null);
                    }
                    DaySchedule daySchedule = scheduledDays.get(day);
                    return daySchedule != null
                            ? DayScheduleInput.fromDomain(daySchedule)
                            : new DayScheduleInput(day, null, null);
                })
                .toList();

        return new WeekScheduleInput(weekRole, schedule);
    }
}
