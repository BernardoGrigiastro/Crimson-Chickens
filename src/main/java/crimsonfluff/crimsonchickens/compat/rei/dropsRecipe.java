package crimsonfluff.crimsonchickens.compat.rei;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public record dropsRecipe(ItemStack input, List<ItemStack> outputs) {
    public dropsRecipe(ItemStack input, List<ItemStack> outputs) {
        this.input = input;
        this.outputs = outputs;
    }

    public ItemStack getInput() {
        return input;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }

    public List<EntryIngredient> reiInput() {
        return Collections.singletonList(EntryIngredients.of(input));
    }

    public List<EntryIngredient> reiOutputs() {
        return outputs.stream().map(EntryIngredients::of).toList();
    }
}