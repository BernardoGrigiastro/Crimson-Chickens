package crimsonfluff.crimsonchickens.compat.rei;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public record breedRecipe(ItemStack input, ItemStack input2, ItemStack output) {
    public breedRecipe(ItemStack input, ItemStack input2, ItemStack output) {
        this.input = input;
        this.input2 = input2;
        this.output = output;
    }

    public EntryIngredient reiInput() {
        return EntryIngredients.of(input);
    }
    public EntryIngredient reiInput2() {
        return EntryIngredients.of(input2);
    }
    public EntryIngredient reiOutput() { return EntryIngredients.of(output); }
}
