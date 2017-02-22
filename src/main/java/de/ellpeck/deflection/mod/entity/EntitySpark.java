package de.ellpeck.deflection.mod.entity;

import de.ellpeck.deflection.api.iface.IDeflector;
import de.ellpeck.deflection.api.iface.ISpark;
import de.ellpeck.deflection.mod.Deflection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntitySpark extends Entity implements ISpark{

    protected EnumFacing facing;
    protected double motion;

    public EntitySpark(World world){
        super(world);
    }

    public EntitySpark(World world, EnumFacing facing, double motion){
        this(world);
        this.facing = facing;
        this.motion = motion;
    }

    public EntitySpark(World world, double x, double y, double z, EnumFacing facing, double motion){
        this(world, facing, motion);
        this.setPosition(x, y, z);
    }

    @Override
    protected void entityInit(){

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound){

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound){

    }

    @Override
    public void onUpdate(){
        super.onUpdate();

        if(!this.world.isRemote){
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;

            BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);
            IBlockState state = this.world.getBlockState(pos);
            Block block = state.getBlock();
            if(block instanceof IDeflector){
                ((IDeflector)block).deflect(this.world, pos, state, this);
            }
            else if(this.posY >= this.world.getHeight()+64 || state.isFullBlock()){
                this.kill();
            }
        }
        else{
            Deflection.proxy.spawnMagicParticle(this.world, (float)(this.prevPosX+(this.posX-this.prevPosX)*5F), (float)(this.prevPosY+(this.posY-this.prevPosY)*5F), (float)(this.prevPosZ+(this.posZ-this.prevPosZ)*5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0xFFFFFF, 3F, 30, 0F, false);
        }
    }

    @Override
    public double getX(){
        return this.posX;
    }

    @Override
    public double getY(){
        return this.posY;
    }

    @Override
    public double getZ(){
        return this.posZ;
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
    public void setPos(double x, double y, double z){
        this.setPosition(x, y, z);
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