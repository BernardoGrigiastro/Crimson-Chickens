package crimsonfluff.crimsonchickens.client;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.entity.ResourceChickenRenderer;
import crimsonfluff.crimsonchickens.init.initBlocks;
import crimsonfluff.crimsonchickens.init.initRegistry;
import crimsonfluff.crimsonchickens.init.initTiles;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class initClient implements ClientModInitializer {
    public static final Identifier DUCK_EGG_SPAWN_PACKET = new Identifier(CrimsonChickens.MOD_ID, "duck_egg_spawn_packet");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(initRegistry.DUCK_EGG, FlyingItemEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(initBlocks.NEST_BLOCK, RenderLayer.getCutout());
        BlockEntityRendererRegistry.register(initTiles.NEST_BLOCK_TILE, NestRenderer::new);

        initRegistry.MOD_CHICKENS.forEach((id, resourceChicken) -> EntityRendererRegistry.register(resourceChicken, context -> {
            ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(id);
            return new ResourceChickenRenderer(context, chickenData);
        }));

        receiveEntityPacket();
//        ItemTooltipCallback.EVENT.register(this::getTooltip);   // TODO: temporary until REI is done
    }

    public void receiveEntityPacket() {
        ClientSidePacketRegistry.INSTANCE.register(DUCK_EGG_SPAWN_PACKET, (ctx, byteBuf) -> {
            EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
            UUID uuid = byteBuf.readUuid();
            int entityId = byteBuf.readVarInt();
            Vec3d pos = DuckEggProjectileSpawnPacket.PacketBufUtil.readVec3d(byteBuf);
            float pitch = DuckEggProjectileSpawnPacket.PacketBufUtil.readAngle(byteBuf);
            float yaw = DuckEggProjectileSpawnPacket.PacketBufUtil.readAngle(byteBuf);
            ClientWorld world = MinecraftClient.getInstance().world;

            ctx.getTaskQueue().execute(() -> {
                if (world == null)
                    throw new IllegalStateException("Tried to spawn entity in a null world !");

                Entity entity = et.create(world);
                if (entity == null)
                    throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\" !");

                entity.updateTrackedPosition(pos);
                entity.setPos(pos.x, pos.y, pos.z);
                entity.setPitch(pitch);
                entity.setBodyYaw(yaw);
                entity.setId(entityId);
                entity.setUuid(uuid);

                world.addEntity(entityId, entity);
            });
        });
    }


//    // TODO: temporary until REI is done
//    public void getTooltip(ItemStack itemStack, TooltipContext tooltipContext, List<Text> tooltipLines) {
//        if (itemStack.getItem() instanceof SpawnEggItem) {
//            // TODO: if begins with MOD_ID its a resource chicken spawn egg
//
//            String ss = Registry.ITEM.getId(itemStack.getItem()).toString();        // TODO: is there a better way than this?
//            ss = ss.substring(0, ss.length() - 10);      // remove '_spawn_egg'
//
//            ResourceChickenData child = ChickenRegistry.getRegistry().getChickenDataFromID(ss);
//            if (child != null) {
//                if (! (child.parentA.isEmpty() && child.parentB.isEmpty())) {
//                    tooltipLines.add(new LiteralText("ParentA: " + child.parentA.substring(16)).formatted(Formatting.GRAY));   // remove mod name
//                    tooltipLines.add(new LiteralText("ParentB: " + child.parentB.substring(16)).formatted(Formatting.GRAY));   // remove mod name
//                }
//                if (child.spawnNaturally)
//                    tooltipLines.add(new LiteralText("Spawns Naturally"));
//            }
//        }
//    }
}
