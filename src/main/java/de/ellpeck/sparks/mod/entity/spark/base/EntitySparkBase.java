package de.ellpeck.sparks.mod.entity.spark.base;

import de.ellpeck.sparks.api.iface.ISpark;
import de.ellpeck.sparks.api.iface.ISparkInteractor;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class EntitySparkBase extends Entity implements ISpark{

    protected static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntitySparkBase.class, DataSerializers.VARINT);

    protected BlockPos lastInteractor = BlockPos.ORIGIN;

    public EntitySparkBase(World world){
        super(world);
    }

    @Override
    public void onUpdate(){
        super.onUpdate();

        if(!this.world.isRemote){
            this.setPosition(this.posX+this.motionX, this.posY+this.motionY, this.posZ+this.motionZ);

            BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);
            IBlockState state = this.world.getBlockState(pos);
            Block block = state.getBlock();

            List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
            state.addCollisionBoxToList(this.world, pos, this.getEntityBoundingBox(), list, this);
            if(!list.isEmpty()){
                boolean shouldCollide = true;

                if(block instanceof ISparkInteractor){
                    ISparkInteractor interactor = (ISparkInteractor)block;

                    EnumActionResult result = interactor.interact(this.world, pos, state, this);
                    if(result == EnumActionResult.SUCCESS || result == EnumActionResult.PASS){
                        shouldCollide = false;
                    }

                    if(!this.isDead){
                        this.onInteraction(interactor, pos, state, result);
                    }
                }

                if(shouldCollide && !this.isDead){
                    this.onCollide(list);
                    return;
                }
            }

            if(this.posY >= this.world.getHeight()+64){
                this.kill();
            }
        }
    }

    protected void onCollide(List<AxisAlignedBB> list){
        this.kill();
    }

    protected void onInteraction(ISparkInteractor block, BlockPos pos, IBlockState state, EnumActionResult result){

    }

    @Override
    protected void entityInit(){
        this.dataManager.register(COLOR, 0);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound){
        this.setColor(compound.getInteger("Color"));
        this.lastInteractor = BlockPos.fromLong(compound.getLong("LastInteractor"));
        this.ticksExisted = compound.getInteger("TicksExisted");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound){
        compound.setInteger("Color", this.getColor());
        compound.setLong("LastInteractor", this.lastInteractor.toLong());
        compound.setInteger("TicksExisted", this.ticksExisted);
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
    public void setPos(double x, double y, double z){
        this.setPosition(x, y, z);
    }

    @Override
    public void setColor(int color){
        this.dataManager.set(COLOR, color);
    }

    @Override
    public int getColor(){
        return this.dataManager.get(COLOR);
    }

    @Override
    public BlockPos getLastInteractor(){
        return this.lastInteractor;
    }

    @Override
    public void setLastInteractor(BlockPos pos){
        this.lastInteractor = pos;
    }

    @Override
    public void setKilled(){
        this.kill();
    }

    @Override
    public EnumPushReaction getPushReaction(){
        return EnumPushReaction.IGNORE;
    }
}
