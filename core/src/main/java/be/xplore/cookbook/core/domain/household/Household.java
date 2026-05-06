package be.xplore.cookbook.core.domain.household;

import be.xplore.cookbook.core.domain.user.User;

import java.util.ArrayList;
import java.util.List;

public record Household(HouseholdId id, User creator, List<User> members, String name, String description) {
    public Household(String name, String description, User creator) {
        this(HouseholdId.create(), creator, new ArrayList<>(), name, description);
    }
}
