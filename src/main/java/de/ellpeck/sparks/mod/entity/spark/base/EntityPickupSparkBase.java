package de.ellpeck.sparks.mod.entity.spark.base;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityPickupSparkBase extends EntitySparkBase{

    private Vec3d homePos;

    public EntityPickupSparkBase(World world){
        super(world);
    }

    public EntityPickupSparkBase(World world, double x, double y, double z, Vec3d homePos){
        super(world);
        this.homePos = homePos;

        this.setPosition(x, y, z);
    }

    @Override
    public void onUpdate(){
        super.onUpdate();

        if(!this.world.isRemote){
            if(this.canGoToGoal()){
                Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ);
                Vec3d goal = this.getGoal();
                Vec3d dist = goal.subtract(pos);

                if(dist.lengthSquared() < 0.01){
                    this.onGoalReached();
                }
                else{
                    Vec3d motion = dist.normalize().scale(0.05);
                    this.motionX = motion.xCoord;
                    this.motionY = motion.yCoord;
                    this.motionZ = motion.zCoord;
                }
            }
            else if(this.canGoHome()){
                Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ);
                Vec3d dist = this.homePos.subtract(pos);

                if(dist.lengthSquared() < 0.01){
                    this.onHomeReached();
                }
                else{
                    Vec3d motion = dist.normalize().scale(0.05);
                    this.motionX = motion.xCoord;
                    this.motionY = motion.yCoord;
                    this.motionZ = motion.zCoord;
                }
            }

            else{
                this.onIdle();
            }
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound){
        super.writeEntityToNBT(compound);

        compound.setDouble("HomeX", this.homePos.xCoord);
        compound.setDouble("HomeY", this.homePos.yCoord);
        compound.setDouble("HomeZ", this.homePos.zCoord);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound){
        super.readEntityFromNBT(compound);

        double x = compound.getDouble("HomeX");
        double y = compound.getDouble("HomeY");
        double z = compound.getDouble("HomeZ");
        this.homePos = new Vec3d(x, y, z);
    }

    protected abstract boolean canGoHome();

    protected void onHomeReached(){

    }

    protected void onGoalReached(){

    }

    protected abstract boolean canGoToGoal();

    protected abstract Vec3d getGoal();

    protected void onIdle(){

    }
}
