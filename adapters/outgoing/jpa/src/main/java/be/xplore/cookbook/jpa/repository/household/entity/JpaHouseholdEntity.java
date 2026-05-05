package be.xplore.cookbook.jpa.repository.household.entity;

import be.xplore.cookbook.core.domain.household.Household;
import be.xplore.cookbook.core.domain.household.HouseholdId;
import be.xplore.cookbook.jpa.repository.user.entity.JpaUserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "households")
public class JpaHouseholdEntity {
    @Id
    private UUID id;

    @Column
    private String name;

    @Column
    private String description;

    @ManyToMany
    private List<JpaUserEntity> members;

    @ManyToOne
    private JpaUserEntity creator;

    protected JpaHouseholdEntity() {
    }

    public JpaHouseholdEntity(UUID id, String name, String description,
                              List<JpaUserEntity> members, JpaUserEntity creator) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.members = members;
        this.creator = creator;
    }

    public Household toDomain() {
        return new Household(new HouseholdId(id), creator.toDomain(),
                members.stream().map(JpaUserEntity::toDomain).toList(), name, description);
    }

    public static JpaHouseholdEntity fromDomain(Household household) {
        return new JpaHouseholdEntity(
                household.id().id(),
                household.name(),
                household.description(),
                household.members().stream().map(JpaUserEntity::fromDomain).toList(),
                JpaUserEntity.fromDomain(household.creator())
        );
    }
}
