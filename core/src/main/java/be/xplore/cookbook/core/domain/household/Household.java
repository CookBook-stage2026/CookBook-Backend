package be.xplore.cookbook.core.domain.household;

import be.xplore.cookbook.core.domain.user.User;

import java.util.ArrayList;
import java.util.List;

public record Household(HouseholdId id, User creator, List<User> members, String name, String description) {
    public Household(String name, String description, User creator) {
        this(HouseholdId.create(), creator, new ArrayList<>(), name, description);
    }

    public Household addMember(User user) {
        boolean isCreator = creator.id().equals(user.id());
        boolean alreadyMember = members.stream().anyMatch(m -> m.id().equals(user.id()));
        if (isCreator || alreadyMember) {
            throw new IllegalArgumentException("User is already a member of the household");
        }
        List<User> updatedMembers = new ArrayList<>(members);
        updatedMembers.add(user);
        return new Household(id, creator, updatedMembers, name, description);
    }
}
