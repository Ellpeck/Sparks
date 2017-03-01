package de.ellpeck.sparks.mod.tile;

import de.ellpeck.sparks.api.cap.IPotentialHandler;
import de.ellpeck.sparks.api.cap.SparksCapabilities;
import de.ellpeck.sparks.mod.block.BlockPotentialSparkInitiator;
import de.ellpeck.sparks.mod.entity.spark.EntityPotentialSpark;
import de.ellpeck.sparks.mod.entity.spark.pickup.EntityPotentialPickupSpark;
import de.ellpeck.sparks.mod.util.CachedEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class TilePotentialSparkInitiator extends TileBase implements ITickable{

    public final Map<BlockPos, CachedEntity<EntityPotentialPickupSpark>> connectedHandlers = new ConcurrentHashMap<BlockPos, CachedEntity<EntityPotentialPickupSpark>>();
    public int accumulatedPotential;

    @Override
    public void update(){
        if(!this.world.isRemote){
            if(this.world.getTotalWorldTime()%100 == 0){
                for(Entry<BlockPos, CachedEntity<EntityPotentialPickupSpark>> entry : this.connectedHandlers.entrySet()){
                    boolean shouldRemove = true;

                    BlockPos pos = entry.getKey();
                    TileEntity tile = this.world.getTileEntity(pos);

                    if(tile != null && tile.hasCapability(SparksCapabilities.capabilityPotential, null)){
                        IPotentialHandler cap = tile.getCapability(SparksCapabilities.capabilityPotential, null);
                        if(cap != null){
                            shouldRemove = false;

                            if(cap.extractPotential(Integer.MAX_VALUE, true) > 0){
                                CachedEntity<EntityPotentialPickupSpark> cache = entry.getValue();
                                if(!cache.validate(this.world)){
                                    Vec3d emitPos = new Vec3d(this.pos.getX()+0.5, this.pos.getY()+0.9, this.pos.getZ()+0.5);
                                    EntityPotentialPickupSpark spark = new EntityPotentialPickupSpark(this.world, emitPos.xCoord, emitPos.yCoord, emitPos.zCoord, emitPos, pos);
                                    spark.setColor(0x33822C);
                                    spark.setLastInteractor(this.pos);
                                    this.world.spawnEntity(spark);

                                    cache.set(spark);
                                }
                            }
                        }
                    }

                    if(shouldRemove){
                        this.connectedHandlers.remove(pos);
                    }
                }
            }

            if(this.world.getTotalWorldTime()%40 == 0){
                this.sendPotentialPacketSpark();
            }
        }
    }

    private void sendPotentialPacketSpark(){
        int threshold = 2000;
        if(this.accumulatedPotential >= threshold){
            IBlockState state = this.world.getBlockState(this.pos);
            EnumFacing facing = state.getValue(BlockPotentialSparkInitiator.FACING);

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

            EntityPotentialSpark spark = new EntityPotentialSpark(this.world, this.pos.getX()+emitX, this.pos.getY()+0.5, this.pos.getZ()+emitZ, facing, 0.1, threshold, threshold);
            spark.setColor(0x97B4FF);
            spark.setLastInteractor(this.pos);
            this.world.spawnEntity(spark);

            this.accumulatedPotential -= threshold;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound){
        this.connectedHandlers.clear();

        NBTTagList list = compound.getTagList("Connections", 10);
        for(int i = 0; i < list.tagCount(); i++){
            NBTTagCompound tag = list.getCompoundTagAt(i);

            BlockPos pos = BlockPos.fromLong(tag.getLong("Pos"));
            CachedEntity<EntityPotentialPickupSpark> cache = new CachedEntity<EntityPotentialPickupSpark>();
            cache.readFromNBT(tag);

            this.connectedHandlers.put(pos, cache);
        }

        this.accumulatedPotential = compound.getInteger("Accumulated");
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound){
        NBTTagList list = new NBTTagList();
        for(Entry<BlockPos, CachedEntity<EntityPotentialPickupSpark>> entry : this.connectedHandlers.entrySet()){
            NBTTagCompound tag = new NBTTagCompound();

            tag.setLong("Pos", entry.getKey().toLong());
            entry.getValue().writeToNBT(tag);

            list.appendTag(tag);
        }
        compound.setTag("Connections", list);

        compound.setInteger("Accumulated", this.accumulatedPotential);
        return super.writeToNBT(compound);
    }
}
