package be.xplore.cookbook.rest.dto.household.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record EditHouseholdDto(@NotNull UUID householdId, @NotEmpty String name, @NotNull String description) {
}
