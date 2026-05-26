package be.xplore.cookbook.ai.dto;

import be.xplore.cookbook.core.domain.recipe.Macro;

import java.util.List;

public record SuggestedMacros(List<Macro> macros) {
}
