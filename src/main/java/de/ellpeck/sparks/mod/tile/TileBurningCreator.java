package de.ellpeck.sparks.mod.tile;

import com.google.common.base.Predicate;
import de.ellpeck.sparks.api.cap.PotentialStorage;
import de.ellpeck.sparks.api.cap.SparksCapabilities;
import de.ellpeck.sparks.mod.Sparks;
import de.ellpeck.sparks.mod.block.BlockBurningCreator;
import de.ellpeck.sparks.mod.entity.spark.pickup.EntityItemPickupSpark;
import de.ellpeck.sparks.mod.util.CachedEntity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;

import java.util.List;

public class TileBurningCreator extends TileBase implements ITickable{

    private static final Predicate<EntityItem> PREDICATE = new Predicate<EntityItem>(){
        @Override
        public boolean apply(EntityItem input){
            if(EntityItemPickupSpark.PICKUP_PREDICATE.apply(input)){
                ItemStack stack = input.getEntityItem();
                if(TileEntityFurnace.getItemBurnTime(stack) > 0){
                    return true;
                }
            }
            return false;
        }
    };

    private final PotentialStorage storage = new PotentialStorage(10000, 0, 1000);

    private int maxBurnTime;
    private int currBurnTime;

    private int lastBurnTime;
    private int lastStorage;

    private final CachedEntity<EntityItemPickupSpark> cachedSpark = new CachedEntity<EntityItemPickupSpark>();

    @Override
    public void update(){
        if(!this.world.isRemote){
            if(this.currBurnTime > 0){
                this.currBurnTime--;

                this.storage.receiveInternal(5, false);
            }

            if(this.currBurnTime <= 0 && !this.storage.isNearlyFull(200)){
                if(this.world.getTotalWorldTime()%40 == 0){

                    if(!this.cachedSpark.validate(this.world)){
                        double x = this.pos.getX()+0.5;
                        double y = this.pos.getY()+0.5;
                        double z = this.pos.getZ()+0.5;
                        AxisAlignedBB aabb = new AxisAlignedBB(x-5, y-5, z-5, x+5, y+5, z+5);
                        List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class, aabb, PREDICATE);

                        if(items != null && !items.isEmpty()){
                            EntityItem item = items.get(this.world.rand.nextInt(items.size()));

                            EnumFacing facing = this.world.getBlockState(this.pos).getValue(BlockBurningCreator.FACING);
                            double emitX;
                            double emitZ;

                            if(facing == EnumFacing.NORTH){
                                emitX = 0.5;
                                emitZ = 0.1;
                            }
                            else if(facing == EnumFacing.EAST){
                                emitX = 0.9;
                                emitZ = 0.5;
                            }
                            else if(facing == EnumFacing.SOUTH){
                                emitX = 0.5;
                                emitZ = 0.9;
                            }
                            else{
                                emitX = 0.1;
                                emitZ = 0.5;
                            }

                            Vec3d emitPos = new Vec3d(this.pos.getX()+emitX, this.pos.getY()+0.15, this.pos.getZ()+emitZ);
                            EntityItemPickupSpark spark = new EntityItemPickupSpark(this.world, emitPos.xCoord, emitPos.yCoord, emitPos.zCoord, emitPos, item, 1);
                            spark.setColor(0x33822C);
                            spark.setLastInteractor(this.pos);
                            this.world.spawnEntity(spark);

                            this.cachedSpark.set(spark);
                        }
                    }
                }
            }

            if((this.lastBurnTime != this.currBurnTime || this.lastStorage != this.storage.getPotential()) && this.world.getTotalWorldTime()%20 == 0){
                this.lastBurnTime = this.currBurnTime;
                this.lastStorage = this.storage.getPotential();

                this.sendToClient();
            }
        }
        else{
            if(this.currBurnTime > 0){
                if(this.world.rand.nextFloat() >= 0.3F){
                    double x = this.pos.getX()+0.5+this.world.rand.nextGaussian()*0.06;
                    double y = this.pos.getY()+0.55;
                    double z = this.pos.getZ()+0.5+this.world.rand.nextGaussian()*0.06;

                    Sparks.proxy.spawnMagicParticle(this.world, x, y, z, 0, this.world.rand.nextFloat()*0.04, 0, 0xFF6600, 2F, 40, 0F, false);
                }
            }
        }
    }

    public boolean fuel(ItemStack stack){
        if(this.currBurnTime <= 0){
            int time = TileEntityFurnace.getItemBurnTime(stack);
            this.maxBurnTime = time;
            this.currBurnTime = time;

            this.cachedSpark.clear();

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

        this.cachedSpark.writeToNBT(compound);
        this.storage.writeToNBT(compound);
    }

    @Override
    public void readSyncedNBT(NBTTagCompound compound){
        super.readSyncedNBT(compound);

        this.currBurnTime = compound.getInteger("CurrBurn");
        this.maxBurnTime = compound.getInteger("MaxBurn");

        this.cachedSpark.readFromNBT(compound);
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
