package de.ellpeck.sparks.mod.entity.spark.pickup;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import de.ellpeck.sparks.mod.Sparks;
import de.ellpeck.sparks.mod.entity.spark.base.EntityPickupSparkBase;
import de.ellpeck.sparks.mod.packet.PacketHandler;
import de.ellpeck.sparks.mod.packet.PacketParticleExplosion;
import de.ellpeck.sparks.mod.util.CachedEntity;
import de.ellpeck.sparks.mod.util.ModUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityItemPickupSpark extends EntityPickupSparkBase{

    public static final Predicate<EntityItem> PICKUP_PREDICATE = new Predicate<EntityItem>(){
        @Override
        public boolean apply(EntityItem input){
            if(!input.isDead){
                NBTTagCompound data = input.getEntityData();
                return !data.getBoolean(ModUtil.MOD_ID+"Pickup");
            }
            else{
                return false;
            }
        }
    };

    private static final DataParameter<Optional<ItemStack>> CARRYING_STACK = EntityDataManager.createKey(EntityItemPickupSpark.class, DataSerializers.OPTIONAL_ITEM_STACK);

    private final CachedEntity<EntityItem> targetItem = new CachedEntity<EntityItem>();
    private int amountToPickUp;

    public EntityItemPickupSpark(World world){
        super(world);
    }

    public EntityItemPickupSpark(World world, double x, double y, double z, Vec3d homePos, EntityItem targetItem, int amountToPickUp){
        super(world, x, y, z, homePos);
        this.targetItem.set(targetItem);
        this.amountToPickUp = amountToPickUp;

        NBTTagCompound data = targetItem.getEntityData();
        data.setBoolean(ModUtil.MOD_ID+"Pickup", true);
    }

    @Override
    protected void entityInit(){
        super.entityInit();
        this.dataManager.register(CARRYING_STACK, Optional.<ItemStack>absent());
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound){
        super.writeEntityToNBT(compound);

        ItemStack carrying = this.getCarryingStack();
        if(carrying != null){
            compound.setTag("CarryingItem", carrying.writeToNBT(new NBTTagCompound()));
        }
        else{
            this.targetItem.writeToNBT(compound);
            compound.setInteger("AmountToPickUp", this.amountToPickUp);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound){
        super.readEntityFromNBT(compound);

        if(compound.hasKey("CarryingItem")){
            NBTTagCompound tag = compound.getCompoundTag("CarryingItem");
            this.setCarryingStack(ItemStack.func_77949_a(tag));
        }
        else{
            this.targetItem.readFromNBT(compound);
            this.amountToPickUp = compound.getInteger("AmountToPickUp");
        }
    }

    @Override
    public void onUpdate(){
        super.onUpdate();

        if(this.world.isRemote){
            Sparks.proxy.spawnMagicParticle(this.world, (float)(this.prevPosX+(this.posX-this.prevPosX)*5F), (float)(this.prevPosY+(this.posY-this.prevPosY)*5F), (float)(this.prevPosZ+(this.posZ-this.prevPosZ)*5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), this.getColor(), 2F, 30, 0F, false);
        }
    }

    @Override
    protected void onGoalReached(){
        EntityItem target = this.targetItem.get(this.world);
        if(target != null && !this.isDead){
            ItemStack stack = target.getEntityItem();
            if(stack != null){
                ItemStack copy = stack.copy();
                copy.stackSize = this.amountToPickUp;
                this.setCarryingStack(copy);

                stack.stackSize -= this.amountToPickUp;
                if(stack.stackSize <= 0){
                    target.setDead();
                }
                else{
                    NBTTagCompound data = target.getEntityData();
                    data.removeTag(ModUtil.MOD_ID+"Pickup");
                }

                PacketParticleExplosion packet = new PacketParticleExplosion(this.posX, this.posY, this.posZ, this.getColor(), 20, 0.02, 4F, false);
                PacketHandler.sendToAllAround(this.world, this.posX, this.posY, this.posZ, packet);
            }

            this.targetItem.clear();
        }
    }

    @Override
    protected boolean canGoToGoal(){
        return this.targetItem.validate(this.world);
    }

    @Override
    protected boolean canGoHome(){
        return this.getCarryingStack() != null;
    }

    @Override
    protected Vec3d getGoal(){
        EntityItem item = this.targetItem.get(this.world);
        return new Vec3d(item.posX, item.posY+0.25, item.posZ);
    }

    @Override
    protected void onHomeReached(){
        this.kill();
    }

    @Override
    protected void onIdle(){
        this.kill();
    }

    @Override
    public void setDead(){
        super.setDead();

        if(!this.world.isRemote){
            ItemStack stack = this.getCarryingStack();
            if(stack != null){
                this.setCarryingStack(null);

                this.entityDropItem(stack, 0F);
            }
            else{
                if(this.targetItem.isValid(this.world)){
                    EntityItem target = this.targetItem.get(this.world);
                    NBTTagCompound data = target.getEntityData();
                    data.removeTag(ModUtil.MOD_ID+"Pickup");
                }
            }
        }
    }

    public ItemStack getCarryingStack(){
        return this.dataManager.get(CARRYING_STACK).orNull();
    }

    public void setCarryingStack(ItemStack stack){
        this.dataManager.set(CARRYING_STACK, Optional.fromNullable(stack));
    }
}
