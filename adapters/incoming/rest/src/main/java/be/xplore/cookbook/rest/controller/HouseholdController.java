package be.xplore.cookbook.rest.controller;

import be.xplore.cookbook.core.domain.household.command.CreateHouseholdCommand;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.service.HouseHoldService;
import be.xplore.cookbook.rest.dto.request.CreateHouseholdDto;
import be.xplore.cookbook.rest.dto.response.HouseholdDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/households")
public class HouseholdController {
    private final HouseHoldService houseHoldService;

    public HouseholdController(HouseHoldService houseHoldService) {
        this.houseHoldService = houseHoldService;
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public HouseholdDto createHousehold(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CreateHouseholdDto request
    ) {
        return HouseholdDto.fromDomain(houseHoldService.createHouseHold(new CreateHouseholdCommand(
                request.name(),
                request.description(),
                new UserId(UUID.fromString(jwt.getSubject()))
        )));
    }
}
