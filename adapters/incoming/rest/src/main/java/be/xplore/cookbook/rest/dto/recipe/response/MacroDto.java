package be.xplore.cookbook.rest.dto.recipe.response;

import be.xplore.cookbook.core.domain.ingredient.MacroType;
import be.xplore.cookbook.core.domain.recipe.Macro;

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
