package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.household.HouseholdRepository;
import be.xplore.cookbook.core.domain.household.command.CreateHouseholdCommand;
import be.xplore.cookbook.core.repository.UserRepository;

public class HouseholdService {
    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;

    public HouseholdService(HouseholdRepository householdRepository, UserRepository userRepository) {
        this.householdRepository = householdRepository;
        this.userRepository = userRepository;
    }

    public Household createHouseHold(CreateHouseholdCommand command) {
        Household household = new Household(command.name(), command.description(),
                userRepository.findById(command.creatorId()).orElseThrow(UserNotFoundException::new));
        return householdRepository.save(household);
    }
}
