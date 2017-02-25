package de.ellpeck.deflection.mod.entity;

import de.ellpeck.deflection.mod.Deflection;
import de.ellpeck.deflection.mod.entity.render.RenderPickupSpark;
import de.ellpeck.deflection.mod.entity.render.RenderTravellingSpark;
import de.ellpeck.deflection.mod.entity.spark.EntityPickupSpark;
import de.ellpeck.deflection.mod.entity.spark.EntityTravellingSpark;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EntityManager{

    private static int nextId;

    public static void preInit(){
        register(EntityTravellingSpark.class, "spark_travelling");
        register(EntityPickupSpark.class, "spark_pickup");
    }

    @SideOnly(Side.CLIENT)
    public static void preInitClient(){
        RenderingRegistry.registerEntityRenderingHandler(EntityTravellingSpark.class, RenderTravellingSpark.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityPickupSpark.class, RenderPickupSpark.FACTORY);
    }

    private static void register(Class<? extends Entity> theClass, String name){
        EntityRegistry.registerModEntity(theClass, name, nextId, Deflection.instance, 64, 1, true);
        nextId++;
    }
}
