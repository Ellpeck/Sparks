package de.ellpeck.sparks.mod.tile;

import com.google.common.base.Predicate;
import de.ellpeck.sparks.api.cap.PotentialStorage;
import de.ellpeck.sparks.api.cap.SparksCapabilities;
import de.ellpeck.sparks.mod.Sparks;
import de.ellpeck.sparks.mod.entity.spark.EntityPickupSpark;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;

import java.util.List;

public class TileBurningCreator extends TileBase implements ITickable{

    private static final Predicate<EntityItem> PREDICATE = new Predicate<EntityItem>(){
        @Override
        public boolean apply(EntityItem input){
            if(EntityPickupSpark.PICKUP_PREDICATE.apply(input)){
                ItemStack stack = input.getEntityItem();
                if(stack.stackSize == 1 && TileEntityFurnace.getItemBurnTime(stack) > 0){
                    return true;
                }
            }
            return false;
        }
    };

    private final PotentialStorage storage = new PotentialStorage(10000, 0, 50);

    private int maxBurnTime;
    private int currBurnTime;

    private int lastBurnTime;
    private int lastStorage;

    private int spawnedPickupSparkId = -1;

    @Override
    public void update(){
        if(!this.world.isRemote){
            if(this.currBurnTime > 0){
                this.currBurnTime--;

                this.storage.receiveInternal(5, false);
            }

            if(this.currBurnTime <= 0 && !this.storage.isNearlyFull(200)){
                if(this.world.getTotalWorldTime()%100 == 0){
                    boolean shouldTry = false;

                    if(this.spawnedPickupSparkId != -1){
                        Entity entity = this.world.getEntityByID(this.spawnedPickupSparkId);
                        if(entity == null || !(entity instanceof EntityPickupSpark)){
                            shouldTry = true;
                        }
                    }
                    else{
                        shouldTry = true;
                    }

                    if(shouldTry){
                        double x = this.pos.getX()+0.5;
                        double y = this.pos.getY()+0.5;
                        double z = this.pos.getZ()+0.5;

                        AxisAlignedBB aabb = new AxisAlignedBB(x-5, y-5, z-5, x+5, y+5, z+5);
                        List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class, aabb, PREDICATE);

                        if(items != null && !items.isEmpty()){
                            EntityItem item = items.get(this.world.rand.nextInt(items.size()));

                            EntityPickupSpark spark = new EntityPickupSpark(this.world, x, y, z, this.pos, item);
                            spark.setColor(0xF0FF26);
                            this.world.spawnEntity(spark);
                            this.spawnedPickupSparkId = spark.getEntityId();
                        }
                    }
                }
            }

            if((this.lastBurnTime != this.currBurnTime || this.lastStorage != this.storage.getPotential()) && this.world.getTotalWorldTime()%40 == 0){
                this.lastBurnTime = this.currBurnTime;
                this.lastStorage = this.storage.getPotential();

                this.sendToClient();
            }
        }
        else{
            if(this.currBurnTime > 0){
                if(this.world.rand.nextFloat() >= 0.25F){
                    double x = this.pos.getX()+0.5+this.world.rand.nextGaussian()*0.1;
                    double y = this.pos.getY()+1.1;
                    double z = this.pos.getZ()+0.5+this.world.rand.nextGaussian()*0.1;

                    Sparks.proxy.spawnMagicParticle(this.world, x, y, z, 0, this.world.rand.nextFloat()*0.03, 0, 0xB50006, 2F, 40, 0F, false);
                }
            }
        }
    }

    public boolean fuel(ItemStack stack){
        if(this.currBurnTime <= 0 && stack.stackSize == 1){
            int time = TileEntityFurnace.getItemBurnTime(stack);
            this.maxBurnTime = time;
            this.currBurnTime = time;

            this.spawnedPickupSparkId = -1;

            this.sendToClient();
            this.markDirty();

            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void writeSyncedNBT(NBTTagCompound compound){
        super.writeSyncedNBT(compound);

        compound.setInteger("CurrBurn", this.currBurnTime);
        compound.setInteger("MaxBurn", this.maxBurnTime);
        compound.setInteger("PickupId", this.spawnedPickupSparkId);
        this.storage.writeToNBT(compound);
    }

    @Override
    public void readSyncedNBT(NBTTagCompound compound){
        super.readSyncedNBT(compound);

        this.currBurnTime = compound.getInteger("CurrBurn");
        this.maxBurnTime = compound.getInteger("MaxBurn");
        this.spawnedPickupSparkId = compound.getInteger("PickupId");
        this.storage.readFromNBT(compound);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        return capability == SparksCapabilities.capabilityPotential;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing){
        if(capability == SparksCapabilities.capabilityPotential){
            return (T)this.storage;
        }
        else{
            return super.getCapability(capability, facing);
        }
    }
}
