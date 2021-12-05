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

public class dropsCategory implements DisplayCategory<dropsDisplay> {
    @Override
    public CategoryIdentifier<? extends dropsDisplay> getCategoryIdentifier() { return ReiPlugin.REI_DROPS; }

    @Override
    public Renderer getIcon() { return EntryStacks.of(initItems.EGG_DUCK); }

    @Override
    public Text getTitle() {
        return new LiteralText("Chicken Drops");
    }

    @Override
    public int getDisplayHeight() {
        return 40;
    }

    @Override
    public int getDisplayWidth(dropsDisplay display) {
        return 60;
    }

    @Override
    public List<Widget> setupDisplay(dropsDisplay display, Rectangle bounds) {
//        Identifier location = new Identifier("textures/gui/breeding.png");
//
//        List<Widget> widgets = new ArrayList<>();
//        widgets.add(Widgets.createTexturedWidget(location, bounds.x, bounds.y, 44, 15, 97, 64));

        var widgets = new ArrayList<Widget>();
//        widgets.add(Widgets.createRecipeBase(bounds));

//        // Plus Glyph
//        widgets.add(new GlyphWidget(bounds, bounds.getMinX() + PLUS_X, bounds.getMinY() + PLUS_Y, GLYPH_WIDTH, GLYPH_HEIGHT, GLYPHS, PLUS_U, PLUS_V));
//        // Arrow Glyph
//        widgets.add(new GlyphWidget(bounds, bounds.getMinX() + ARROW_X, bounds.getMinY() + ARROW_Y, GLYPH_WIDTH, GLYPH_HEIGHT, GLYPHS, ARROW_U, ARROW_V));

        var inputs = display.getInputEntries();
        var outputs = display.getOutputEntries();

        var reactant = inputs.get(0);
        var product = outputs.get(0);

        widgets.add(Widgets.createSlot(new Point(bounds.getMinX(), bounds.getMinY())).entries(reactant));
        widgets.add(Widgets.createSlot(new Point(bounds.getMinX() + 64, bounds.getMinY())).entries(product));

        return widgets;
    }

    //public static final Identifier GLYPHS = new Identifier(CrimsonChickens.MOD_ID, "textures/gui/rei/glyphs.png");
}
