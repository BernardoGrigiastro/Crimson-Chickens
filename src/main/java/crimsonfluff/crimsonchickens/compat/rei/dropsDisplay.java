package crimsonfluff.crimsonchickens.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.ArrayList;
import java.util.List;

public record dropsDisplay(dropsRecipe recipe) implements Display {
    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ReiPlugin.REI_DROPS;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        var outputs = new ArrayList<EntryIngredient>();
        outputs.addAll(recipe.reiOutputs());
        return outputs;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return recipe.reiInput();
    }
}
