package be.xplore.cookbook.rest.dto.household.response;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.rest.dto.user.response.UserDto;

import java.util.List;
import java.util.UUID;

public record HouseholdDto(UUID id, String name, String description, List<UserDto> members, UserDto creator) {
    public static HouseholdDto fromDomain(Household household) {
        return new HouseholdDto(
                household.id().id(),
                household.name(),
                household.description(),
                household.members().stream().map(UserDto::fromDomain).toList(),
                UserDto.fromDomain(household.creator())
        );
    }
}
