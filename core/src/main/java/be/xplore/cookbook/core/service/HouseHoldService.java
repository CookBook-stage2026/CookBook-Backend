package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.household.HouseholdRepository;
import be.xplore.cookbook.core.domain.household.command.CreateHouseholdCommand;
import be.xplore.cookbook.core.repository.UserRepository;

public class HouseHoldService {
    private final HouseholdRepository houseHoldRepository;
    private final UserRepository userRepository;

    public HouseHoldService(HouseholdRepository houseHoldRepository, UserRepository userRepository) {
        this.houseHoldRepository = houseHoldRepository;
        this.userRepository = userRepository;
    }

    public Household createHouseHold(CreateHouseholdCommand command) {
        Household household = new Household(command.name(), command.description(),
                userRepository.findById(command.creatorId()).orElseThrow(UserNotFoundException::new));
        return houseHoldRepository.save(household);
    }
}
