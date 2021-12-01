package crimsonfluff.crimsonchickens.compat.rei;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.init.initItems;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReiPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<dropsDisplay> REI_DROPS = CategoryIdentifier.of(new Identifier(CrimsonChickens.MOD_ID, "rei.drops"));
    public static final CategoryIdentifier<breedDisplay> REI_BREED = CategoryIdentifier.of(new Identifier(CrimsonChickens.MOD_ID, "rei.breed"));

    @Override
    public String getPluginProviderName() { return "reiChickens"; }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new dropsCategory());
        registry.add(new breedCategory());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registerChickenDrops(registry);
        registerChickenBreed(registry);
    }

    private void registerChickenBreed(DisplayRegistry registry) {
        ChickenRegistry.getRegistry().getChickens().forEach((id, chickenData) -> {
            if (! (chickenData.parentA.isEmpty() && chickenData.parentB.isEmpty())) {
                ResourceChickenData parentA = ChickenRegistry.getRegistry().getChickenDataFromID(chickenData.parentA);
                ResourceChickenData parentB = ChickenRegistry.getRegistry().getChickenDataFromID(chickenData.parentB);

                registry.add(new breedDisplay(new breedRecipe(new ItemStack(chickenData.spawnEggItem), new ItemStack(parentA.spawnEggItem), new ItemStack(parentB.spawnEggItem))));
            }
        });
    }

    private void registerChickenDrops(DisplayRegistry registry) {
        ChickenRegistry.getRegistry().getChickens().forEach((id, chickenData) -> {
            List<ItemStack> lst = new ArrayList<>();
            ItemStack itemStack = ItemStack.EMPTY;

            if (chickenData.dropItemItem.startsWith("items:")) {
                Tag<Item> iTag = ItemTags.getTagGroup().getTag(new Identifier(chickenData.dropItemItem.substring(6)));
                if (iTag != null)
                    itemStack = new ItemStack(iTag.values().get(0));
            }
            else if (chickenData.dropItemItem.startsWith("blocks:")) {
                Tag<Block> iTag = BlockTags.getTagGroup().getTag(new Identifier(chickenData.dropItemItem.substring(7)));
                if (iTag != null)
                    itemStack = new ItemStack(iTag.values().get(0));
            }
            else
                itemStack = new ItemStack(Registry.ITEM.get(new Identifier(chickenData.dropItemItem)));

            if (! itemStack.isEmpty()) {
                if (chickenData.dropItemNBT != null) itemStack.setNbt(chickenData.dropItemNBT.copy());
                lst.add(itemStack.copy());
            }

            lst.add(new ItemStack(chickenData.hasTrait == 1 ? initItems.FEATHER_DUCK : Items.FEATHER));

            var recipes = new ArrayList<dropsRecipe>();
            lst.forEach(itm -> {
                recipes.add(new dropsRecipe(new ItemStack(chickenData.spawnEggItem), Collections.singletonList(itm)));
            });
            recipes.forEach(rcp -> {
                registry.add(new dropsDisplay(rcp));
            });

            // passing list of item stack to output does not loop thru output items, like jei does
            //            registry.add(new dropsDisplay(new dropsRecipe(new ItemStack(chickenData.spawnEggItem), lst)));

//                FabricaeExNihiloRegistries.SIEVE.getREIRecipes().forEach(recipe ->
//                registry.add(new SieveDisplay(recipe)));
        });
    }

    private static void addDescription(DisplayRegistry registry) {
        //registry.add(new SimpleGridMenuDisplay().);
    }
}
