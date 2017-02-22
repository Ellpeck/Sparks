package de.ellpeck.deflection.mod.entity;

import de.ellpeck.deflection.Deflection;
import de.ellpeck.deflection.api.iface.IDeflector;
import de.ellpeck.deflection.api.iface.ISpark;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntitySpark extends Entity implements ISpark{

    public EntitySpark(World world){
        super(world);
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
    public double getMotionX(){
        return this.motionX;
    }

    @Override
    public double getMotionY(){
        return this.motionY;
    }

    @Override
    public double getMotionZ(){
        return this.motionZ;
    }

    @Override
    public void setMotion(double x, double y, double z){
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }
}
