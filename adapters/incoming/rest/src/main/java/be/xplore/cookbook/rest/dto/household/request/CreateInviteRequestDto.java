package be.xplore.cookbook.rest.dto.household.request;

import jakarta.validation.constraints.Positive;

public record CreateInviteRequestDto(@Positive Integer durationMinutes) {
}
