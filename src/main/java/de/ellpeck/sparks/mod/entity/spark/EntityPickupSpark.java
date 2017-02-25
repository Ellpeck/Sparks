package de.ellpeck.sparks.mod.entity.spark;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import de.ellpeck.sparks.mod.Sparks;
import de.ellpeck.sparks.mod.packet.PacketHandler;
import de.ellpeck.sparks.mod.packet.PacketParticleExplosion;
import de.ellpeck.sparks.mod.util.ModUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityPickupSpark extends EntitySparkBase{

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
    private BlockPos emittedPos;

    private int itemWaitingCooldown;

    public EntityPickupSpark(World world){
        super(world);
    }

    public EntityPickupSpark(World world, double x, double y, double z, BlockPos emittedPos, EntityItem targetItem){
        super(world);
        this.emittedPos = emittedPos;
        this.targetItem = targetItem;

        NBTTagCompound data = this.targetItem.getEntityData();
        data.setBoolean(ModUtil.MOD_ID+"Pickup", true);

        this.setPosition(x, y, z);
    }

    @Override
    protected void entityInit(){
        super.entityInit();
        this.dataManager.register(CARRYING_STACK, Optional.<ItemStack>absent());
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound){
        super.writeEntityToNBT(compound);

        compound.setLong("EmittedPos", this.emittedPos.toLong());
        compound.setInteger("Cooldown", this.itemWaitingCooldown);

        ItemStack carrying = this.getCarryingStack();
        if(carrying != null){
            compound.setTag("CarryingItem", carrying.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound){
        super.readEntityFromNBT(compound);

        this.emittedPos = BlockPos.fromLong(compound.getLong("EmittedPos"));
        this.itemWaitingCooldown = compound.getInteger("Cooldown");

        if(compound.hasKey("CarryingItem")){
            NBTTagCompound tag = compound.getCompoundTag("CarryingItem");
            this.setCarryingStack(ItemStack.func_77949_a(tag));
        }
    }

    @Override
    public void onUpdate(){
        super.onUpdate();

        if(!this.world.isRemote){
            if(this.getCarryingStack() == null){
                if(this.targetItem != null && !this.targetItem.isDead){
                    Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ);
                    Vec3d itemPos = new Vec3d(this.targetItem.posX, this.targetItem.posY+0.25, this.targetItem.posZ);
                    Vec3d dist = itemPos.subtract(pos);

                    Vec3d motion = dist.normalize().scale(0.05);
                    this.motionX = motion.xCoord;
                    this.motionY = motion.yCoord;
                    this.motionZ = motion.zCoord;

                    if(dist.lengthSquared() < 0.01){
                        this.setCarryingStack(this.targetItem.getEntityItem());

                        PacketParticleExplosion packet = new PacketParticleExplosion(this.posX, this.posY, this.posZ, this.getColor(), 20, 0.02, 4F, false);
                        PacketHandler.sendToAllAround(this.world, this.posX, this.posY, this.posZ, packet);

                        this.targetItem.setDead();
                        this.targetItem = null;
                    }
                }
                else{
                    this.kill();
                }
            }
            else{
                Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ);
                Vec3d homePos = new Vec3d(this.emittedPos.getX()+0.5, this.emittedPos.getY()+0.5, this.emittedPos.getZ()+0.5);
                Vec3d dist = homePos.subtract(pos);

                Vec3d motion = dist.normalize().scale(0.05);
                this.motionX = motion.xCoord;
                this.motionY = motion.yCoord;
                this.motionZ = motion.zCoord;
            }
        }
        else{
            Sparks.proxy.spawnMagicParticle(this.world, (float)(this.prevPosX+(this.posX-this.prevPosX)*5F), (float)(this.prevPosY+(this.posY-this.prevPosY)*5F), (float)(this.prevPosZ+(this.posZ-this.prevPosZ)*5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), this.getColor(), 2F, 40, 0F, false);
        }
    }

    @Override
    protected void kill(){
        super.kill();

        if(!this.world.isRemote){
            ItemStack stack = this.getCarryingStack();
            if(stack != null){
                this.setCarryingStack(null);

                this.entityDropItem(stack, 0F);
            }
            else{
                if(this.targetItem != null){
                    NBTTagCompound data = this.targetItem.getEntityData();
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
