package be.xplore.cookbook.rest.controller;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.household.command.CreateHouseholdCommand;
import be.xplore.cookbook.core.domain.household.command.DeleteByIdCommand;
import be.xplore.cookbook.core.domain.household.command.FindAllHouseholdsForUserCommand;
import be.xplore.cookbook.core.domain.household.command.FindHouseholdByIdQuery;
import be.xplore.cookbook.core.domain.household.command.RemoveMemberFromHouseholdCommand;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.service.HouseholdService;
import be.xplore.cookbook.rest.dto.request.CreateHouseholdDto;
import be.xplore.cookbook.rest.dto.response.HouseholdDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/households")
public class HouseholdController {
    private final HouseholdService householdService;

    public HouseholdController(HouseholdService householdService) {
        this.householdService = householdService;
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public HouseholdDto createHousehold(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CreateHouseholdDto request
    ) {
        return HouseholdDto.fromDomain(householdService.createHouseHold(new CreateHouseholdCommand(
                request.name(),
                request.description(),
                getUserIdFromJwt(jwt)
        )));
    }

    @GetMapping
    public List<HouseholdDto> getAllHouseholds(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return householdService.findAllHouseholdsForUserId(new FindAllHouseholdsForUserCommand(
                getUserIdFromJwt(jwt)
        )).stream().map(HouseholdDto::fromDomain).toList();
    }

    @GetMapping("/{id}")
    public HouseholdDto getHouseholdById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        Household household = householdService.findHouseholdById(
                new FindHouseholdByIdQuery(new HouseholdId(id), getUserIdFromJwt(jwt))
        );
        return HouseholdDto.fromDomain(household);
    }

    @DeleteMapping("/{id}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMemberFromHousehold(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @PathVariable UUID userId
    ) {
        householdService.removeMemberFromHousehold(
                new RemoveMemberFromHouseholdCommand(new HouseholdId(id), getUserIdFromJwt(jwt), new UserId(userId))
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        householdService.deleteById(
                new DeleteByIdCommand(new HouseholdId(id), getUserIdFromJwt(jwt))
        );
    }

    private UserId getUserIdFromJwt(Jwt jwt) {
        return new UserId(UUID.fromString(jwt.getSubject()));
    }
}
