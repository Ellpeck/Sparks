package de.ellpeck.sparks.mod.entity;

import de.ellpeck.sparks.mod.Sparks;
import de.ellpeck.sparks.mod.entity.render.RenderPickupSpark;
import de.ellpeck.sparks.mod.entity.render.RenderSparkBase;
import de.ellpeck.sparks.mod.entity.spark.base.EntitySparkBase;
import de.ellpeck.sparks.mod.entity.spark.pickup.EntityItemPickupSpark;
import de.ellpeck.sparks.mod.entity.spark.EntityPotentialSpark;
import de.ellpeck.sparks.mod.entity.spark.pickup.EntityPotentialPickupSpark;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EntityManager{

    private static int nextId;

    public static void preInit(){
        register(EntityPotentialSpark.class, "spark_potential");
        register(EntityItemPickupSpark.class, "spark_item_pickup");
        register(EntityPotentialPickupSpark.class, "spark_potential_pickup");
    }

    @SideOnly(Side.CLIENT)
    public static void preInitClient(){
        RenderingRegistry.registerEntityRenderingHandler(EntitySparkBase.class, RenderSparkBase.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityItemPickupSpark.class, RenderPickupSpark.FACTORY);
    }

    private static void register(Class<? extends Entity> theClass, String name){
        EntityRegistry.registerModEntity(theClass, name, nextId, Sparks.instance, 64, 1, true);
        nextId++;
    }
}
