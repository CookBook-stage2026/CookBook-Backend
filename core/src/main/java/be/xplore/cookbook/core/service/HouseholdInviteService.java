package be.xplore.cookbook.core.service;

import be.xplore.cookbook.core.domain.exception.ForbiddenException;
import be.xplore.cookbook.core.domain.exception.NotFoundException;
import be.xplore.cookbook.core.domain.exception.UserNotFoundException;
import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.household.exception.HouseholdNotFoundException;
import be.xplore.cookbook.core.domain.householdinvite.HouseholdInvite;
import be.xplore.cookbook.core.domain.householdinvite.HouseholdInviteToken;
import be.xplore.cookbook.core.domain.householdinvite.InviteTokenGenerator;
import be.xplore.cookbook.core.domain.householdinvite.command.AcceptInviteCommand;
import be.xplore.cookbook.core.domain.householdinvite.command.CreateInviteCommand;
import be.xplore.cookbook.core.domain.householdinvite.command.FindHouseholdInvitationByTokenQuery;
import be.xplore.cookbook.core.domain.householdinvite.command.RevokeInviteCommand;
import be.xplore.cookbook.core.domain.user.User;
import be.xplore.cookbook.core.repository.HouseholdInviteRepository;
import be.xplore.cookbook.core.repository.HouseholdRepository;
import be.xplore.cookbook.core.repository.UserRepository;

import java.time.Duration;

public class HouseholdInviteService {
    private final UserRepository userRepository;
    private final HouseholdInviteRepository householdInviteRepository;
    private final HouseholdRepository householdRepository;
    private final Duration defaultInviteDuration;
    private final Duration minInviteDuration;
    private final Duration maxInviteDuration;

    public HouseholdInviteService(UserRepository userRepository, HouseholdInviteRepository householdInviteRepository,
                                  HouseholdRepository householdRepository,
                                  Duration defaultInviteDuration,
                                  Duration minInviteDuration, Duration maxInviteDuration) {
        this.userRepository = userRepository;
        this.householdInviteRepository = householdInviteRepository;
        this.householdRepository = householdRepository;
        this.defaultInviteDuration = defaultInviteDuration;
        this.minInviteDuration = minInviteDuration;
        this.maxInviteDuration = maxInviteDuration;
    }

    public HouseholdInvite findByToken(FindHouseholdInvitationByTokenQuery query) {
        return householdInviteRepository.findByTokenHash(InviteTokenGenerator.hash(query.token()))
                .orElseThrow(() ->
                        new NotFoundException("Invite not found."));
    }

    public HouseholdInviteToken createInvite(CreateInviteCommand command) {
        Duration duration = resolveDuration(command.duration());
        Household household = householdRepository.findById(command.householdId())
                .orElseThrow(() -> new HouseholdNotFoundException(command.householdId()));
        if (!household.creator().id().equals(command.requesterId())) {
            throw new ForbiddenException("Only the household creator can create invites!");
        }
        String plainToken = InviteTokenGenerator.generate();
        String tokenHash = InviteTokenGenerator.hash(plainToken);
        HouseholdInvite invite = new HouseholdInvite(command.householdId(), tokenHash, duration, command.requesterId());
        return new HouseholdInviteToken(householdInviteRepository.save(invite), plainToken);
    }

    public Household acceptInvite(AcceptInviteCommand command) {
        String tokenHash = InviteTokenGenerator.hash(command.token());
        HouseholdInvite invite = householdInviteRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new NotFoundException("Invite not found for token: " + command.token()));
        if (!invite.isValid()) {
            throw new IllegalArgumentException("Invite is not valid!");
        }
        User user = userRepository.findById(command.userId()).orElseThrow(UserNotFoundException::new);
        Household household = householdRepository.findById(invite.householdId())
                .orElseThrow(() -> new HouseholdNotFoundException(invite.householdId()));
        return householdRepository.save(household.addMember(user));
    }

    public void revokeInvite(RevokeInviteCommand command) {
        HouseholdInvite invite = householdInviteRepository.findById(command.inviteId())
                .orElseThrow(() -> new NotFoundException("Invite not found: " + command.inviteId()));
        if (!invite.householdId().equals(command.householdId())) {
            throw new NotFoundException("Household invite not found because household id's don't match!");
        }
        Household household = householdRepository.findById(command.householdId())
                .orElseThrow(() -> new HouseholdNotFoundException(command.householdId()));
        if (!household.creator().id().equals(command.requesterId())) {
            throw new ForbiddenException("Only the household creator can revoke this invite!");
        }
        householdInviteRepository.save(invite.revoke());
    }

    private Duration resolveDuration(Duration requested) {
        Duration duration = requested != null ? requested : defaultInviteDuration;
        if (duration.compareTo(minInviteDuration) < 0 || duration.compareTo(maxInviteDuration) > 0) {
            throw new IllegalArgumentException("The given duration is invalid!");
        }
        return duration;
    }
}
