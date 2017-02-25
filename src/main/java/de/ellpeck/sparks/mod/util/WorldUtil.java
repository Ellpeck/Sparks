package de.ellpeck.sparks.mod.util;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.UUID;

public final class WorldUtil{

    public static Entity getEntityByUUID(World world, UUID id){
        if(id != null){
            for(Entity entity : world.loadedEntityList){
                if(id.equals(entity.getUniqueID())){
                    return entity;
                }
            }
        }
        return null;
    }

}
