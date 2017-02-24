package de.ellpeck.deflection.mod.entity.spark;

import de.ellpeck.deflection.api.iface.ISpark;
import de.ellpeck.deflection.api.iface.ISparkInteractor;
import de.ellpeck.deflection.mod.Deflection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntitySpark extends Entity implements ISpark{

    protected static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntitySpark.class, DataSerializers.VARINT);

    protected EnumFacing facing;
    protected double motion;

    protected int currLifetime;
    protected int maxLifetime = 800;

    protected BlockPos lastInteractor = BlockPos.ORIGIN;

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
        this.dataManager.register(COLOR, 0);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound){
        this.setColor(compound.getInteger("Color"));

        this.motion = compound.getDouble("Motion");
        this.facing = EnumFacing.getFront(compound.getInteger("Facing"));

        this.maxLifetime = compound.getInteger("MaxLifetime");
        this.currLifetime = compound.getInteger("CurrLifetime");

        this.lastInteractor = BlockPos.fromLong(compound.getLong("LastInteractor"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound){
        compound.setInteger("Color", this.getColor());

        compound.setDouble("Motion", this.motion);
        compound.setInteger("Facing", this.facing.getIndex());

        compound.setInteger("MaxLifetime", this.maxLifetime);
        compound.setInteger("CurrLifetime", this.currLifetime);

        compound.setLong("LastInteractor", this.lastInteractor.toLong());
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

            this.setPosition(this.posX+this.motionX, this.posY+this.motionY, this.posZ+this.motionZ);

            BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);
            IBlockState state = this.world.getBlockState(pos);
            Block block = state.getBlock();

            if(!(block instanceof ISparkInteractor) || !((ISparkInteractor)block).interact(this.world, pos, state, this)){
                List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
                state.addCollisionBoxToList(this.world, pos, this.getEntityBoundingBox(), list, this, false);

                if(!list.isEmpty()){
                    this.kill();
                    return;
                }
            }

            if(this.posY >= this.world.getHeight()+64){
                this.kill();
            }
        }
        else{
            Deflection.proxy.spawnMagicParticle(this.world, (float)(this.prevPosX+(this.posX-this.prevPosX)*5F), (float)(this.prevPosY+(this.posY-this.prevPosY)*5F), (float)(this.prevPosZ+(this.posZ-this.prevPosZ)*5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), this.dataManager.get(COLOR), 2F, 40, 0F, false);
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
    public boolean split(ISparkInteractor splitter, BlockPos pos, IBlockState state, EnumFacing firstDir, EnumFacing secondDir){
        for(int i = 0; i < 2; i++){
            EntitySpark spark = new EntitySpark(this.world, this.posX, this.posY, this.posZ, i == 0 ? firstDir : secondDir, this.motion);
            spark.maxLifetime = (this.maxLifetime-this.currLifetime)/2;
            spark.setColor(this.getColor());
            spark.setLastInteractor(pos);
            this.world.spawnEntity(spark);
        }
        this.kill();

        return true;
    }
}