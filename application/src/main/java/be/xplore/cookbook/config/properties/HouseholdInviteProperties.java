package be.xplore.cookbook.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "household.invite")
public record HouseholdInviteProperties(
        long defaultDurationMinutes,
        long minDurationMinutes,
        long maxDurationMinutes
) {
}
