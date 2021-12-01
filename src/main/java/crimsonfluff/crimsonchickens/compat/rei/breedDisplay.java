package crimsonfluff.crimsonchickens.compat.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.ArrayList;
import java.util.List;

public record breedDisplay(breedRecipe recipe) implements Display {
    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ReiPlugin.REI_BREED;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        var outputs = new ArrayList<EntryIngredient>();
        outputs.add(recipe.reiOutput());
        return outputs;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        var outputs = new ArrayList<EntryIngredient>();
        outputs.add(recipe.reiInput());
        outputs.add(recipe.reiInput2());
        return outputs;
    }
}
