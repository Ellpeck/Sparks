package de.ellpeck.deflection.mod.entity;

import de.ellpeck.deflection.mod.Deflection;
import de.ellpeck.deflection.mod.entity.render.RenderSpark;
import de.ellpeck.deflection.mod.entity.spark.EntitySpark;
import de.ellpeck.deflection.mod.util.ModUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EntityManager{

    public static void preInit(){
        EntityRegistry.registerModEntity(new ResourceLocation(ModUtil.MOD_ID, "spark"), EntitySpark.class, "spark", 0, Deflection.instance, 64, 1, false);
    }

    @SideOnly(Side.CLIENT)
    public static void preInitClient(){
        RenderingRegistry.registerEntityRenderingHandler(EntitySpark.class, RenderSpark.FACTORY);
    }
}
