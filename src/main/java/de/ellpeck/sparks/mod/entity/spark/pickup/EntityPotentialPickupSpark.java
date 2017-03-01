package de.ellpeck.sparks.mod.entity.spark.pickup;

import de.ellpeck.sparks.api.cap.IPotentialHandler;
import de.ellpeck.sparks.api.cap.SparksCapabilities;
import de.ellpeck.sparks.mod.Sparks;
import de.ellpeck.sparks.mod.entity.spark.base.EntityPickupSparkBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EntityPotentialPickupSpark extends EntityPickupSparkBase{

    private static final DataParameter<Integer> POTENTIAL_AMOUNT = EntityDataManager.createKey(EntityPotentialPickupSpark.class, DataSerializers.VARINT);

    public EntityPotentialPickupSpark(World world){
        super(world);
    }

    private boolean pickedUpPotential;
    private BlockPos targetPos;

    public EntityPotentialPickupSpark(World world, double x, double y, double z, Vec3d homePos, BlockPos targetPos){
        super(world, x, y, z, homePos);

        this.targetPos = targetPos;
    }

    @Override
    protected void entityInit(){
        super.entityInit();

        this.dataManager.register(POTENTIAL_AMOUNT, 0);
    }

    @Override
    public void onUpdate(){
        super.onUpdate();

        if(this.world.isRemote){
            Sparks.proxy.spawnMagicParticle(this.world, (float)(this.prevPosX+(this.posX-this.prevPosX)*5F), (float)(this.prevPosY+(this.posY-this.prevPosY)*5F), (float)(this.prevPosZ+(this.posZ-this.prevPosZ)*5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), this.getColor(), 2F, 30, 0F, false);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound){
        super.writeEntityToNBT(compound);

        compound.setBoolean("PickedUpPotential", this.pickedUpPotential);
        compound.setLong("TargetPos", this.targetPos.toLong());
        compound.setInteger("Potential", this.getPotential());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound){
        super.readEntityFromNBT(compound);

        this.pickedUpPotential = compound.getBoolean("PickedUpPotential");
        this.targetPos = BlockPos.fromLong(compound.getLong("TargetPos"));
        this.setPotential(compound.getInteger("Potential"));
    }

    @Override
    protected boolean canGoHome(){
        return this.pickedUpPotential;
    }

    @Override
    protected boolean canGoToGoal(){
        return !this.pickedUpPotential;
    }

    @Override
    protected Vec3d getGoal(){
        return new Vec3d(this.targetPos.getX()+0.5, this.targetPos.getY()+0.5, this.targetPos.getZ()+0.5);
    }

    @Override
    protected void onGoalReached(){
        TileEntity tile = this.world.getTileEntity(this.targetPos);
        if(tile != null && tile.hasCapability(SparksCapabilities.capabilityPotential, null)){
            IPotentialHandler cap = tile.getCapability(SparksCapabilities.capabilityPotential, null);
            if(cap != null){
                this.setPotential(cap.extractPotential(1000, false));

                this.pickedUpPotential = true;
                return;
            }
        }

        this.kill();
    }

    @Override
    protected boolean shouldCollide(BlockPos pos, IBlockState state, Block block, List<AxisAlignedBB> list){
        return super.shouldCollide(pos, state, block, list) && !this.targetPos.equals(pos);
    }

    @Override
    protected void onIdle(){
        this.kill();
    }

    @Override
    protected void onHomeReached(){
        this.kill();
    }

    public void setPotential(int potential){
        this.dataManager.set(POTENTIAL_AMOUNT, potential);
    }

    public int getPotential(){
        return this.dataManager.get(POTENTIAL_AMOUNT);
    }
}
