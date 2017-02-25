package de.ellpeck.sparks.mod.entity.spark.base;

import de.ellpeck.sparks.api.iface.ISparkInteractor;
import de.ellpeck.sparks.api.iface.ITravellingSpark;
import de.ellpeck.sparks.mod.Sparks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class EntityTravellingSparkBase extends EntitySparkBase implements ITravellingSpark{

    protected EnumFacing facing;
    protected double motion;

    public EntityTravellingSparkBase(World world){
        super(world);
    }

    public EntityTravellingSparkBase(World world, double x, double y, double z, EnumFacing facing, double motion){
        this(world);
        this.facing = facing;
        this.motion = motion;

        this.setPosition(x, y, z);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound){
        super.readEntityFromNBT(compound);

        this.motion = compound.getDouble("Motion");
        this.facing = EnumFacing.getFront(compound.getInteger("Facing"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound){
        super.writeEntityToNBT(compound);

        compound.setDouble("Motion", this.motion);
        compound.setInteger("Facing", this.facing.getIndex());
    }

    @Override
    public void onUpdate(){
        super.onUpdate();

        if(!this.world.isRemote){
            this.motionX = this.facing.getFrontOffsetX()*this.motion;
            this.motionY = this.facing.getFrontOffsetY()*this.motion;
            this.motionZ = this.facing.getFrontOffsetZ()*this.motion;
        }
    }

    @Override
    public double getMotion(){
        return this.motion;
    }

    @Override
    public EnumFacing getFacing(){
        return this.facing;
    }

    @Override
    public void setMotion(double motion){
        this.motion = motion;
    }

    @Override
    public void setFacing(EnumFacing facing){
        this.facing = facing;
    }
}