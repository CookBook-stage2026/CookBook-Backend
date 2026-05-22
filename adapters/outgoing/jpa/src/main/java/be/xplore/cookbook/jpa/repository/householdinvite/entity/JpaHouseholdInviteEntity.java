package be.xplore.cookbook.jpa.repository.householdinvite.entity;

import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.core.domain.householdinvite.HouseholdInvite;
import be.xplore.cookbook.core.domain.householdinvite.HouseholdInviteId;
import be.xplore.cookbook.core.domain.user.UserId;
import be.xplore.cookbook.jpa.repository.household.entity.JpaHouseholdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "household_invites")
public class JpaHouseholdInviteEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false)
    private UUID createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private JpaHouseholdEntity household;

    protected JpaHouseholdInviteEntity() {
    }

    public JpaHouseholdInviteEntity(UUID id, String tokenHash, Instant expiresAt,
                                    boolean revoked, UUID createdBy, JpaHouseholdEntity household) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdBy = createdBy;
        this.household = household;
    }

    public HouseholdInvite toDomain() {
        return new HouseholdInvite(
                new HouseholdInviteId(id),
                new HouseholdId(household.getId()),
                tokenHash,
                expiresAt,
                revoked,
                new UserId(createdBy)
        );
    }

    public static JpaHouseholdInviteEntity fromDomain(HouseholdInvite invite, JpaHouseholdEntity householdRef) {
        return new JpaHouseholdInviteEntity(
                invite.id().id(),
                invite.tokenHash(),
                invite.expiresAt(),
                invite.revoked(),
                invite.createdBy().id(),
                householdRef
        );
    }
}
