package de.ellpeck.deflection.mod.entity.spark;

import de.ellpeck.deflection.api.iface.ISparkInteractor;
import de.ellpeck.deflection.api.iface.ITravellingSpark;
import de.ellpeck.deflection.mod.Deflection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityTravellingSpark extends EntitySparkBase implements ITravellingSpark{

    protected EnumFacing facing;
    protected double motion;

    protected int currLifetime;
    protected int maxLifetime = 800;

    public EntityTravellingSpark(World world){
        super(world);
    }

    public EntityTravellingSpark(World world, EnumFacing facing, double motion){
        this(world);
        this.facing = facing;
        this.motion = motion;
    }

    public EntityTravellingSpark(World world, double x, double y, double z, EnumFacing facing, double motion){
        this(world, facing, motion);
        this.setPosition(x, y, z);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound){
        super.readEntityFromNBT(compound);

        this.motion = compound.getDouble("Motion");
        this.facing = EnumFacing.getFront(compound.getInteger("Facing"));

        this.maxLifetime = compound.getInteger("MaxLifetime");
        this.currLifetime = compound.getInteger("CurrLifetime");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound){
        super.writeEntityToNBT(compound);

        compound.setDouble("Motion", this.motion);
        compound.setInteger("Facing", this.facing.getIndex());

        compound.setInteger("MaxLifetime", this.maxLifetime);
        compound.setInteger("CurrLifetime", this.currLifetime);
    }

    @Override
    public void onUpdate(){
        super.onUpdate();

        if(!this.world.isRemote){
            this.currLifetime++;
            if(this.currLifetime >= this.maxLifetime){
                this.kill();
                return;
            }

            this.motionX = this.facing.getFrontOffsetX()*this.motion;
            this.motionY = this.facing.getFrontOffsetY()*this.motion;
            this.motionZ = this.facing.getFrontOffsetZ()*this.motion;
        }
        else{
            Deflection.proxy.spawnMagicParticle(this.world, (float)(this.prevPosX+(this.posX-this.prevPosX)*5F), (float)(this.prevPosY+(this.posY-this.prevPosY)*5F), (float)(this.prevPosZ+(this.posZ-this.prevPosZ)*5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), this.getColor(), 2F, 40, 0F, false);
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

    @Override
    public boolean split(ISparkInteractor splitter, BlockPos pos, IBlockState state, EnumFacing firstDir, EnumFacing secondDir){
        for(int i = 0; i < 2; i++){
            EntityTravellingSpark spark = new EntityTravellingSpark(this.world, this.posX, this.posY, this.posZ, i == 0 ? firstDir : secondDir, this.motion);
            spark.maxLifetime = (this.maxLifetime-this.currLifetime)/2;
            spark.setColor(this.getColor());
            spark.setLastInteractor(pos);
            this.world.spawnEntity(spark);
        }
        this.kill();

        return true;
    }
}