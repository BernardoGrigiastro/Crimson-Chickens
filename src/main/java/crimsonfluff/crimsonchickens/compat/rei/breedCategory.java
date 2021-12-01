package crimsonfluff.crimsonchickens.compat.rei;

import crimsonfluff.crimsonchickens.init.initItems;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class breedCategory implements DisplayCategory<breedDisplay> {
    @Override
    public CategoryIdentifier<? extends breedDisplay> getCategoryIdentifier() { return ReiPlugin.REI_BREED; }

    @Override
    public Renderer getIcon() { return EntryStacks.of(initItems.EGG_DUCK); }

    @Override
    public Text getTitle() {
        return new LiteralText("Chicken Breeding");
    }

    @Override
    public int getDisplayHeight() {
        return 40;
    }

    @Override
    public int getDisplayWidth(breedDisplay display) {
        return 60;
    }

    @Override
    public List<Widget> setupDisplay(breedDisplay display, Rectangle bounds) {
//        Identifier location = new Identifier("textures/gui/breeding.png");
//
//        List<Widget> widgets = new ArrayList<>();
//        widgets.add(Widgets.createTexturedWidget(location, bounds.x, bounds.y, 44, 15, 97, 64));

        var widgets = new ArrayList<Widget>();
//        widgets.add(Widgets.createRecipeBase(bounds));

        widgets.add(Widgets.createSlot(new Point(bounds.getMinX(), bounds.getMinY())).entries(display.getInputEntries().get(0)));
        widgets.add(Widgets.createSlot(new Point(bounds.getMinX() + 20, bounds.getMinY())).entries(display.getInputEntries().get(1)));
        widgets.add(Widgets.createSlot(new Point(bounds.getMinX() + 64, bounds.getMinY())).entries(display.getOutputEntries().get(0)));

        return widgets;
    }
}
