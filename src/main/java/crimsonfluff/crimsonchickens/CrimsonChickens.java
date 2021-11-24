package crimsonfluff.crimsonchickens;

import crimsonfluff.crimsonchickens.config.CrimsonChickensConfig;
import crimsonfluff.crimsonchickens.entity.AngryChickenEntity;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import crimsonfluff.crimsonchickens.init.*;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.SpawnSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//  /summon crimsonchickens:blaze ~ ~ ~ {Age:-24000,analyzed:1,strength:10,gain:10,growth:10}
//  /summon crimsonchickens:blaze ~ ~ ~ {analyzed:1,strength:10,gain:10,growth:10}

public class CrimsonChickens implements ModInitializer {
    public static final String MOD_ID = "crimsonchickens";
    public static final Logger LOGGER = LogManager.getLogger(CrimsonChickens.class);
    public static final ItemGroup CREATIVE_TAB = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "tab"), () -> new ItemStack(initItems.EGG_DUCK));

    public static final CrimsonChickensConfig CONFIGURATION = AutoConfig.register(CrimsonChickensConfig.class, GsonConfigSerializer::new).getConfig();

    @Override
    public void onInitialize() {
        initConfigs.register();
        initItems.register();
        initBlocks.register();
        initTiles.register();
        initSounds.register();
        initRegistry.register();

        initChickenConfigs.loadConfigs();

        initRegistry.MOD_CHICKENS.forEach((id, resourceChicken) -> {
            if (id.equals("angry"))
                FabricDefaultAttributeRegistry.register(resourceChicken, AngryChickenEntity.createChickenAttributes(id));
            else
                FabricDefaultAttributeRegistry.register(resourceChicken, ResourceChickenEntity.createChickenAttributes(id));
        });

        Identifier chicken_id = Registry.ENTITY_TYPE.getId(EntityType.CHICKEN);
        BuiltinRegistries.BIOME.forEach(biome -> {
            initRegistry.MOD_CHICKENS.forEach((id, resourceChicken) -> {
                ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(id);

                if (chickenData.spawnNaturally) {
                    if (id.equals("chicken")) {
                        List<SpawnSettings.SpawnEntry> spawns = biome.getSpawnSettings().getSpawnEntries(SpawnGroup.CREATURE).getEntries();
                        if (spawns.stream().anyMatch(tag -> tag.type == EntityType.CHICKEN)) {
                            BiomeModifications.create(chicken_id).add(ModificationPhase.REMOVALS, BiomeSelectors.categories(biome.getCategory()), context -> {
                                context.getSpawnSettings().removeSpawnsOfEntityType(EntityType.CHICKEN);
                            });

                            BiomeModifications.addSpawn(BiomeSelectors.categories(biome.getCategory()), SpawnGroup.CREATURE, resourceChicken, chickenData.spawnWeight, 4, 4);
//                            LOGGER.info("Chicken Added: " + biome);
                        }

                    } else {
                        String biomeString = '"' + biome.toString() + '"';

                        if (chickenData.biomesWhitelist != null) {
                            if (chickenData.biomesWhitelist.toString().contains(biomeString)) {
                                //LOGGER.info("BIOME_WHITELIST: " + biomeString + " : " + s);

                                BiomeModifications.addSpawn(BiomeSelectors.categories(biome.getCategory()), chickenData.spawnType, resourceChicken, chickenData.spawnWeight, 1, 4);
                            }

                        } else if (chickenData.biomesBlacklist != null) {
                            if (! chickenData.biomesBlacklist.toString().contains(biomeString)) {
                                //LOGGER.info("BIOME_BLACKLIST: " + biomeString + " : " + s);

                                BiomeModifications.addSpawn(BiomeSelectors.categories(biome.getCategory()), chickenData.spawnType, resourceChicken, chickenData.spawnWeight, 1, 4);
                            }

                        } else {
                            //LOGGER.info("BIOME_NATURAL: " + biomeString + " : " + s);

                            BiomeModifications.addSpawn(BiomeSelectors.categories(biome.getCategory()), chickenData.spawnType, resourceChicken, chickenData.spawnWeight, 1, 4);
                        }
                    }
                }
            });
        });

        // Vanilla chicken from SpawnEgg/Spawner/Summon
        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if (entity.getType() == EntityType.CHICKEN) {
                entity.remove(Entity.RemovalReason.DISCARDED);

                ResourceChickenEntity entity2 = initRegistry.MOD_CHICKENS.get("chicken").create(serverWorld);
                if (entity2 != null) {
                    entity2.copyPositionAndRotation(entity);

                    NbtCompound nbtCompound = entity.writeNbt(new NbtCompound());
                    nbtCompound.remove("Dimension");
                    nbtCompound.remove("UUID");
                    entity2.readNbt(nbtCompound);

                    serverWorld.spawnEntity(entity2);
                }
            }
        });
    }

    public static int calcNewEggLayTime(Random r, ResourceChickenData rcd, int growth) {
        if (rcd.eggLayTime == 0) return 0;

        int egg = r.nextInt(rcd.eggLayTime) + rcd.eggLayTime;
        return (int) Math.max(1.0f, (egg * (10.f - growth + 1.f)) / 10.f);
    }

    public static int calcDropQuantity(int gain) {
        if (gain < 5) return 1;         // between 1-4
        if (gain < 10) return 2;        // between 5-9
        return 3;                       // 10
    }

    public static List<ItemStack> calcDrops(int gain, ResourceChickenData chickenData, int fortune) {
        // return a list of item drops
        // done like this to avoid making stacks of non-stackable items
        List<ItemStack> lst = new ArrayList<>();

        // TODO: if no drop item then try and find a loot table?
        if (! chickenData.dropItemItem.equals("")) {
            ItemStack itemStack = ItemStack.EMPTY;

            if (chickenData.dropItemItem.startsWith("item:")) {
                Tag<Item> iTag = ItemTags.getTagGroup().getTag(new Identifier(chickenData.dropItemItem.substring(5)));
                if (iTag != null)
                    itemStack = new ItemStack(iTag.values().get(0));
            }
            else if (chickenData.dropItemItem.startsWith("block:")) {
                Tag<Block> iTag = BlockTags.getTagGroup().getTag(new Identifier(chickenData.dropItemItem.substring(6)));
                if (iTag != null)
                    itemStack = new ItemStack(iTag.values().get(0));
            }
            else
                itemStack = new ItemStack(Registry.ITEM.get(new Identifier(chickenData.dropItemItem)));

            if (! itemStack.isEmpty()) {
                if (chickenData.dropItemNBT != null) itemStack.setNbt(chickenData.dropItemNBT.copy());
                int dropQuantity = calcDropQuantity(gain) + fortune;

                if (itemStack.isStackable()) {
                    itemStack.setCount(dropQuantity);

                    lst.add(itemStack);
                }
                else {
                    for (int a = 0; a < dropQuantity; a++) {
                        ItemStack itm = itemStack.copy();
                        lst.add(itm);
                    }
                }
            }
        }

        Random r = new Random();
        if (r.nextInt(8) == 0) lst.add(chickenData.hasTrait == 1 ? new ItemStack(initItems.FEATHER_DUCK) : new ItemStack(Items.FEATHER));

        return lst;
    }

    public static String formatTime(int milli) {
        int secs = milli / 20;
        int mins = secs / 60;
        int hours = mins / 60;

        if (hours == 0)
            return String.format("%02d:%02d", mins, secs % 60);
        else
            return String.format("%02d:%02d:%02d", hours, mins, secs % 60);
    }
}
