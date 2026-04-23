package cookbook.stage.backend.domain.user;

import cookbook.stage.backend.domain.recipe.Recipe;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class User {

    private final UserId id;
    private String email;
    private String displayName;
    private List<SocialConnection> socialConnections;
    private final List<Recipe> recipes = new ArrayList<>();
    private final List<WeekSchedule> weekSchedules = new ArrayList<>();

    public User(String email, String displayName, List<SocialConnection> socialConnections) {
        this.id = UserId.create();
        this.email = email;
        this.displayName = displayName;
        this.socialConnections = socialConnections;
    }

    public User(UserId id, String email, String displayName, List<SocialConnection> socialConnections) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.socialConnections = socialConnections;
    }

    public void assignRecipeToDay(int year, int weekNumber, DayOfWeek day, Recipe recipe) {
        WeekSchedule schedule = getOrCreateWeekSchedule(year, weekNumber);
        schedule.assignRecipe(day, recipe);
    }

    public void skipDay(int year, int weekNumber, DayOfWeek day) {
        findWeekSchedule(year, weekNumber).ifPresent(schedule -> schedule.skipDay(day));
    }

    public Optional<WeekSchedule> findWeekSchedule(int year, int weekNumber) {
        return weekSchedules.stream()
                .filter(s -> s.year() == year && s.weekNumber() == weekNumber)
                .findFirst();
    }

    private WeekSchedule getOrCreateWeekSchedule(int year, int weekNumber) {
        return findWeekSchedule(year, weekNumber).orElseGet(() -> {
            WeekSchedule newSchedule = new WeekSchedule(
                    WeekScheduleId.create(),
                    year,
                    weekNumber,
                    new java.util.EnumMap<>(DayOfWeek.class)
            );
            weekSchedules.add(newSchedule);
            return newSchedule;
        });
    }

    public UserId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<SocialConnection> getSocialConnections() {
        return socialConnections;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public List<WeekSchedule> getWeekSchedules() {
        return Collections.unmodifiableList(weekSchedules);
    }
}
