package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.EggItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class LiquidEgg extends EggItem {
    private final Fluid fluidType;      // Lava, Water etc

//    @Nullable
//    @Override
//    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
//        FluidHandlerItemStack fluidHandlerItemStack = new FluidHandlerItemStack(stack, 1000) {
//            @Nonnull
//            @Override
//            public ItemStack getContainer() { return ItemStack.EMPTY; }
//
//            @Nonnull
//            @Override
//            public FluidStack getFluid() { return new FluidStack(fluidType,1000); }
//
//            @Override
//            public int getTanks() { return 1; }
//
//            @Override
//            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) { return false; }
//
//            @Override
//            public boolean canFillFluidType(FluidStack fluid) { return false; }
//
//            @Nonnull
//            @Override
//            public FluidStack drain(int maxDrain, FluidAction action) {
//                if (maxDrain < 1000) return FluidStack.EMPTY;
//                return super.drain(maxDrain, action);
//            }
//        };
//
//        fluidHandlerItemStack.fill(new FluidStack(fluidType, 1000), IFluidHandler.FluidAction.EXECUTE);
//        return fluidHandlerItemStack;
//    }

    public LiquidEgg(Fluid fluid) {
        super(new FabricItemSettings().group(CrimsonChickens.CREATIVE_TAB).maxCount(16));
        this.fluidType = fluid;
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemStack = playerIn.getStackInHand(handIn);
        BlockHitResult hitResult = BucketItem.raycast(worldIn, playerIn, this.fluidType == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
        if (((HitResult)hitResult).getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(itemStack);
        }
        if (((HitResult)hitResult).getType() == HitResult.Type.BLOCK) {
            BlockPos fluid;
            BlockPos blockPos = hitResult.getBlockPos();
            Direction direction = hitResult.getSide();
            BlockPos blockPos2 = blockPos.offset(direction);

            if (!worldIn.canPlayerModifyAt(playerIn, blockPos) || !playerIn.canPlaceOn(blockPos2, direction, itemStack)) {
                return TypedActionResult.fail(itemStack);
            }

            BlockState blockState = worldIn.getBlockState(blockPos);
            BlockPos blockPos3 = fluid = blockState.getBlock() instanceof FluidFillable && this.fluidType == Fluids.WATER ? blockPos : blockPos2;
            if (this.placeFluid(playerIn, worldIn, fluid, hitResult)) {
//                this.onEmptied(worldIn, itemStack, fluid);
                if (playerIn instanceof ServerPlayerEntity) {
                    Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerIn, fluid, itemStack);
                }
                playerIn.incrementStat(Stats.USED.getOrCreateStat(this));
                return TypedActionResult.success(this.getEmptiedStack(itemStack, playerIn), worldIn.isClient());
            }
            return TypedActionResult.fail(itemStack);
        }

        return TypedActionResult.pass(itemStack);
    }

    protected ItemStack getEmptiedStack(ItemStack stack, PlayerEntity player) {
        return !player.isCreative() ? ItemStack.EMPTY : stack;
    }

    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult blockHitResult) {
        boolean bl2;
        if (!(this.fluidType instanceof FlowableFluid)) {
            return false;
        }
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        Material material = blockState.getMaterial();
        boolean bl = blockState.canBucketPlace(this.fluidType);
        boolean bl3 = bl2 = blockState.isAir() || bl || block instanceof FluidFillable && ((FluidFillable)((Object)block)).canFillWithFluid(world, pos, blockState, this.fluidType);
        if (!bl2) {
            return blockHitResult != null && this.placeFluid(player, world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()), null);
        }
        if (world.getDimension().isUltrawarm() && this.fluidType.isIn(FluidTags.WATER)) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
            for (int l = 0; l < 8; ++l) {
                world.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
            }
            return true;
        }
        if (block instanceof FluidFillable && this.fluidType == Fluids.WATER) {
            ((FluidFillable)((Object)block)).tryFillWithFluid(world, pos, blockState, ((FlowableFluid)this.fluidType).getStill(false));
            this.playEmptyingSound(player, world, pos);
            return true;
        }
        if (!world.isClient && bl && !material.isLiquid()) {
            world.breakBlock(pos, true);
        }
        if (world.setBlockState(pos, this.fluidType.getDefaultState().getBlockState(), 11) || blockState.getFluidState().isStill()) {
            this.playEmptyingSound(player, world, pos);
            return true;
        }
        return false;
    }

    protected void playEmptyingSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos) {
        SoundEvent soundEvent = this.fluidType.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
        world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}
