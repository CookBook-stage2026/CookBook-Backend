package be.xplore.cookbook.rest.dto.household.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record EditHouseholdDto(@NotEmpty String name, @NotNull String description) {
}
