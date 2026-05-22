package be.xplore.cookbook.jpa.repository.ingredient.entity;

import be.xplore.cookbook.core.domain.ingredient.Macro;
import be.xplore.cookbook.core.domain.ingredient.MacroType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class JpaMacroEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "macro_type", nullable = false)
    private MacroType type;

    @Column(name = "value_per_unit", nullable = false)
    private double valuePerUnit;

    protected JpaMacroEmbeddable() {
    }

    private JpaMacroEmbeddable(MacroType type, double valuePerUnit) {
        this.type = type;
        this.valuePerUnit = valuePerUnit;
    }

    public static JpaMacroEmbeddable fromDomain(Macro macro) {
        return new JpaMacroEmbeddable(macro.type(), macro.valuePerUnit());
    }

    public Macro toDomain() {
        return new Macro(type, valuePerUnit);
    }
}
