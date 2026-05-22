package be.xplore.cookbook.rest.dto.response;

import be.xplore.cookbook.core.domain.ingredient.Macro;
import be.xplore.cookbook.core.domain.ingredient.MacroType;

public record MacroDto(
        MacroType type,
        double value
) {
    public static MacroDto fromDomain(Macro macro, double quantity) {
        return new MacroDto(macro.type(), macro.valuePerUnit() * quantity);
    }

    public static MacroDto of(MacroType type, double value) {
        return new MacroDto(type, value);
    }
}
