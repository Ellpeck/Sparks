package de.ellpeck.sparks.mod.entity.spark.pickup;

import de.ellpeck.sparks.api.cap.IPotentialHandler;
import de.ellpeck.sparks.api.cap.SparksCapabilities;
import de.ellpeck.sparks.mod.Sparks;
import de.ellpeck.sparks.mod.entity.spark.base.EntityPickupSparkBase;
import de.ellpeck.sparks.mod.packet.PacketHandler;
import de.ellpeck.sparks.mod.packet.PacketParticleExplosion;
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

    private int pickupAmount;

    private boolean pickedUpPotential;
    private BlockPos targetPos;

    public EntityPotentialPickupSpark(World world){
        super(world);
    }
    public EntityPotentialPickupSpark(World world, double x, double y, double z, Vec3d homePos, BlockPos targetPos, int pickupAmount){
        super(world, x, y, z, homePos);
        this.pickupAmount = pickupAmount;
        this.targetPos = targetPos;

        this.setColor(0x404040);
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
            Sparks.proxy.spawnMagicParticle(this.world, (float)(this.prevPosX+(this.posX-this.prevPosX)*5F), (float)(this.prevPosY+(this.posY-this.prevPosY)*5F), (float)(this.prevPosZ+(this.posZ-this.prevPosZ)*5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), 0.0125F*(this.rand.nextFloat()-0.5F), this.getColor(), 1.5F, 30, 0F, false);
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound){
        super.writeEntityToNBT(compound);

        compound.setInteger("PickupAmount", this.pickupAmount);
        compound.setBoolean("PickedUpPotential", this.pickedUpPotential);
        compound.setLong("TargetPos", this.targetPos.toLong());
        compound.setInteger("Potential", this.getPotential());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound){
        super.readEntityFromNBT(compound);

        this.pickupAmount = compound.getInteger("PickupAmount");
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
        return new Vec3d(this.targetPos.getX()+0.5, this.targetPos.getY()+1.05, this.targetPos.getZ()+0.5);
    }

    @Override
    protected void onGoalReached(){
        TileEntity tile = this.world.getTileEntity(this.targetPos);
        if(tile != null && tile.hasCapability(SparksCapabilities.capabilityPotential, null)){
            IPotentialHandler cap = tile.getCapability(SparksCapabilities.capabilityPotential, null);
            if(cap != null){
                this.setPotential(cap.extractPotential(this.pickupAmount, false));

                this.pickedUpPotential = true;
                this.setColor(0x97B4FF);

                PacketParticleExplosion packet = new PacketParticleExplosion(this.posX, this.posY, this.posZ, this.getColor(), 15, 0.01, 2F, false);
                PacketHandler.sendToAllAround(this.world, this.posX, this.posY, this.posZ, packet);

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
