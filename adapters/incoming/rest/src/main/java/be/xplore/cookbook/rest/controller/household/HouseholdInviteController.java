package be.xplore.cookbook.rest.controller.household;

import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.householdinvite.HouseholdInviteId;
import be.xplore.cookbook.core.domain.householdinvite.command.AcceptInviteCommand;
import be.xplore.cookbook.core.domain.householdinvite.command.CreateInviteCommand;
import be.xplore.cookbook.core.domain.householdinvite.command.FindHouseholdInvitationByTokenQuery;
import be.xplore.cookbook.core.domain.householdinvite.command.FindHouseholdInviteByHouseholdIdQuery;
import be.xplore.cookbook.core.domain.householdinvite.command.RevokeInviteCommand;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.core.service.household.HouseholdInviteService;
import be.xplore.cookbook.rest.dto.household.request.CreateInviteRequestDto;
import be.xplore.cookbook.rest.dto.household.response.HouseholdInviteDto;
import be.xplore.cookbook.rest.dto.household.response.HouseholdInviteResponseDto;
import be.xplore.cookbook.rest.dto.household.response.HouseholdInviteTokenResponseDto;
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

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/household-invites")
public class HouseholdInviteController {
    private final HouseholdInviteService householdInviteService;

    public HouseholdInviteController(HouseholdInviteService householdInviteService) {
        this.householdInviteService = householdInviteService;
    }

    @GetMapping("/{token}")
    public HouseholdInviteDto getInviteByToken(
            @PathVariable String token
    ) {
        var invite = householdInviteService.findByToken(
                new FindHouseholdInvitationByTokenQuery(token));
        return new HouseholdInviteDto(invite.id().id(), invite.revoked());
    }

    @PostMapping("/{householdId}/invites")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public HouseholdInviteTokenResponseDto createInvite(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID householdId,
            @RequestBody(required = false) @Valid CreateInviteRequestDto request
    ) {
        Duration duration = (request != null && request.durationMinutes() != null)
                ? Duration.ofMinutes(request.durationMinutes())
                : null;
        return HouseholdInviteTokenResponseDto.fromDomain(householdInviteService.createInvite(new CreateInviteCommand(
                new HouseholdId(householdId),
                new UserId(UUID.fromString(jwt.getSubject())),
                duration
        )));
    }

    @GetMapping("/{householdId}/invites")
    public List<HouseholdInviteResponseDto> getInvitesForHousehold(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID householdId
    ) {
        return householdInviteService.getInvitesForHousehold(new FindHouseholdInviteByHouseholdIdQuery(
                new HouseholdId(householdId),
                new UserId(UUID.fromString(jwt.getSubject()))
        )).stream().map(HouseholdInviteResponseDto::fromDomain).toList();
    }

    @PostMapping("/invites/{token}/accept")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptInvite(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String token
    ) {
        householdInviteService.acceptInvite(new AcceptInviteCommand(
                token,
                new UserId(UUID.fromString(jwt.getSubject()))
        ));
    }

    @DeleteMapping("/{householdId}/invites/{inviteId}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeInvite(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID householdId,
            @PathVariable UUID inviteId
    ) {
        householdInviteService.revokeInvite(new RevokeInviteCommand(
                new HouseholdInviteId(inviteId),
                new HouseholdId(householdId),
                new UserId(UUID.fromString(jwt.getSubject()))
        ));
    }
}
