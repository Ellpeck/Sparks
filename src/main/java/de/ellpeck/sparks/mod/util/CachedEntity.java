package de.ellpeck.sparks.mod.util;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.UUID;

public class CachedEntity<T extends Entity>{

    private T targetEntity;
    private UUID targetUUID;

    public void writeToNBT(NBTTagCompound compound){
        if(this.targetUUID != null){
            compound.setUniqueId("TargetEntity", this.targetUUID);
        }
    }

    public void readFromNBT(NBTTagCompound compound){
        if(compound.hasUniqueId("TargetEntity")){
            this.targetUUID = compound.getUniqueId("TargetEntity");
        }
    }

    public T get(World world){
        if(this.targetEntity == null){
            Entity entity = WorldUtil.getEntityByUUID(world, this.targetUUID);
            if(entity != null){
                this.targetEntity = (T)entity;
            }
        }

        return this.targetEntity;
    }

    public void set(T targetEntity){
        this.targetEntity = targetEntity;
        this.targetUUID = targetEntity.getUniqueID();
    }

    public void clear(){
        this.targetEntity = null;
        this.targetUUID = null;
    }

    public boolean isValid(World world){
        if(this.targetUUID != null){
            T entity = this.get(world);
            if(entity != null && !entity.isDead){
                return true;
            }
        }
        return false;
    }

    public boolean validate(World world){
        if(!this.isValid(world)){
            this.clear();

            return false;
        }
        else{
            return true;
        }
    }
}
