package be.xplore.cookbook.core.service.household;

import be.xplore.cookbook.core.domain.exception.ForbiddenException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.household.command.CreateHouseholdCommand;
import be.xplore.cookbook.core.domain.household.command.DeleteByIdCommand;
import be.xplore.cookbook.core.domain.household.command.FindAllHouseholdsForUserCommand;
import be.xplore.cookbook.core.domain.household.command.FindHouseholdByIdQuery;
import be.xplore.cookbook.core.domain.household.command.RemoveMemberFromHouseholdCommand;
import be.xplore.cookbook.core.domain.household.exception.HouseholdNotFoundException;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.domain.weekschedule.ScheduleOwner;
import be.xplore.cookbook.core.repository.HouseholdRepository;
import be.xplore.cookbook.core.repository.UserRepository;
import be.xplore.cookbook.core.repository.WeekScheduleRepository;

import java.util.List;

public class HouseholdService {

    private final HouseholdRepository householdRepository;
    private final UserRepository userRepository;
    private final WeekScheduleRepository scheduleRepository;

    public HouseholdService(HouseholdRepository householdRepository, UserRepository userRepository,
                            WeekScheduleRepository scheduleRepository) {
        this.householdRepository = householdRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public Household createHouseHold(CreateHouseholdCommand command) {
        Household household = new Household(command.name(), command.description(),
                userRepository.findById(command.creatorId()).orElseThrow(UserNotFoundException::new));
        return householdRepository.save(household);
    }

    public List<Household> findAllHouseholdsForUserId(FindAllHouseholdsForUserCommand command) {
        return householdRepository.findAllByUserId(command.userId());
    }

    public Household findHouseholdById(FindHouseholdByIdQuery query) {
        User user = userRepository.findById(query.userId())
                .orElseThrow(UserNotFoundException::new);

        Household household = householdRepository.findById(query.householdId())
                .orElseThrow(() -> new HouseholdNotFoundException(query.householdId()));

        if (!household.creator().id().equals(query.userId()) && !household.members().contains(user)) {
            throw new HouseholdNotFoundException(query.householdId());
        }

        return household;
    }

    public void removeMemberFromHousehold(RemoveMemberFromHouseholdCommand command) {
        Household household = householdRepository.findById(command.householdId())
                .orElseThrow(() -> new HouseholdNotFoundException(command.householdId()));

        if (!household.creator().id().equals(command.creatorId()) && !command.creatorId().equals(command.memberId())) {
            throw new ForbiddenException("You are not allowed to remove a member from this household.");
        }

        boolean isRemoved = householdRepository.removeMember(command.householdId(), command.memberId());

        if (isRemoved) {
            scheduleRepository.deleteAllScheduledRecipesFromUser(
                    ScheduleOwner.forHousehold(household.id()), command.memberId());
        }
    }

    public void deleteById(DeleteByIdCommand command) {
        Household household = householdRepository.findById(command.householdId())
                .orElseThrow(() -> new HouseholdNotFoundException(command.householdId()));

        if (!household.creator().id().equals(command.userId())) {
            throw new ForbiddenException("You are not allowed to delete this household.");
        }

        householdRepository.deleteById(command.householdId());
    }
}
