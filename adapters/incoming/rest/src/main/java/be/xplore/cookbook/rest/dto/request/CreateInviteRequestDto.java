package be.xplore.cookbook.rest.dto.request;

import jakarta.validation.constraints.Positive;

public record CreateInviteRequestDto(@Positive Integer durationMinutes) {
}
