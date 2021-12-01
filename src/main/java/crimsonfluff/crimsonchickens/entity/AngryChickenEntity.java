package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class AngryChickenEntity extends ResourceChickenEntity {
    public AngryChickenEntity(EntityType<? extends ResourceChickenEntity> type, World world, ResourceChickenData chickenData) {
        super(type, world, chickenData);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.add(2, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new AngryChickenGoal(this, 1.3D, true));
//        // TODO
//        //        this.goalSelector.add(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.add(5, new AnimalMateGoal(this, 1.0D));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compoundNBT) {
        super.readCustomDataFromNbt(compoundNBT);

        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(this.dataTracker.get(STRENGTH));
    }
}
