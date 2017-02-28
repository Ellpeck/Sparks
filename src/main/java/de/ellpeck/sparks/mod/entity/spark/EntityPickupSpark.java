package de.ellpeck.sparks.mod.entity.spark;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import de.ellpeck.sparks.mod.Sparks;
import de.ellpeck.sparks.mod.entity.spark.base.EntityPickupSparkBase;
import de.ellpeck.sparks.mod.packet.PacketHandler;
import de.ellpeck.sparks.mod.packet.PacketParticleExplosion;
import de.ellpeck.sparks.mod.util.ModUtil;
import de.ellpeck.sparks.mod.util.WorldUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityPickupSpark extends EntityPickupSparkBase{

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

    private static final DataParameter<Optional<ItemStack>> CARRYING_STACK = EntityDataManager.createKey(EntityPickupSpark.class, DataSerializers.OPTIONAL_ITEM_STACK);

    private EntityItem targetItem;
    private UUID targetItemId;
    private int amountToPickUp;

    private int itemWaitingCooldown;

    public EntityPickupSpark(World world){
        super(world);
    }

    public EntityPickupSpark(World world, double x, double y, double z, Vec3d homePos, EntityItem targetItem, int amountToPickUp){
        super(world, x, y, z, homePos);
        this.targetItem = targetItem;
        this.amountToPickUp = amountToPickUp;

        EntityItem target = this.getTargetItem();
        NBTTagCompound data = target.getEntityData();
        data.setBoolean(ModUtil.MOD_ID+"Pickup", true);
        this.targetItemId = target.getUniqueID();
    }

    @Override
    protected void entityInit(){
        super.entityInit();
        this.dataManager.register(CARRYING_STACK, Optional.<ItemStack>absent());
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound){
        super.writeEntityToNBT(compound);

        compound.setInteger("Cooldown", this.itemWaitingCooldown);

        ItemStack carrying = this.getCarryingStack();
        if(carrying != null){
            compound.setTag("CarryingItem", carrying.writeToNBT(new NBTTagCompound()));
        }
        else{
            compound.setUniqueId("TargetItem", this.targetItemId);
            compound.setInteger("AmountToPickUp", this.amountToPickUp);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound){
        super.readEntityFromNBT(compound);

        this.itemWaitingCooldown = compound.getInteger("Cooldown");

        if(compound.hasKey("CarryingItem")){
            NBTTagCompound tag = compound.getCompoundTag("CarryingItem");
            this.setCarryingStack(ItemStack.func_77949_a(tag));
        }
        else{
            this.targetItemId = compound.getUniqueId("TargetItem");
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
        EntityItem target = this.getTargetItem();
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

            this.targetItem = null;
            this.targetItemId = null;
        }
    }

    @Override
    protected boolean canGoToGoal(){
        return this.targetItem != null;
    }

    @Override
    protected boolean canGoHome(){
        return this.getCarryingStack() != null;
    }

    @Override
    protected Vec3d getGoal(){
        return new Vec3d(this.targetItem.posX, this.targetItem.posY+0.25, this.targetItem.posZ);
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
                EntityItem target = this.getTargetItem();
                if(target != null){
                    NBTTagCompound data = target.getEntityData();
                    data.removeTag(ModUtil.MOD_ID+"Pickup");
                }
            }
        }
    }

    public EntityItem getTargetItem(){
        if(this.targetItem == null){
            Entity entity = WorldUtil.getEntityByUUID(this.world, this.targetItemId);
            if(entity != null && entity instanceof EntityItem){
                this.targetItem = (EntityItem)entity;
            }
        }
        return this.targetItem;
    }

    public ItemStack getCarryingStack(){
        return this.dataManager.get(CARRYING_STACK).orNull();
    }

    public void setCarryingStack(ItemStack stack){
        this.dataManager.set(CARRYING_STACK, Optional.fromNullable(stack));
    }
}
