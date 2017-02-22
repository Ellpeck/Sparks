package de.ellpeck.deflection.mod.proxy;

import de.ellpeck.deflection.mod.entity.EntityManager;
import de.ellpeck.deflection.mod.entity.EntitySpark;
import de.ellpeck.deflection.mod.entity.render.RenderSpark;
import de.ellpeck.deflection.mod.event.ClientEvents;
import de.ellpeck.deflection.mod.particle.ParticleHandler;
import de.ellpeck.deflection.mod.particle.ParticleMagic;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy{

    @Override
    public void preInit(FMLPreInitializationEvent event){
        EntityManager.preInitClient();

        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }

    @Override
    public void init(FMLInitializationEvent event){

    }

    @Override
    public void postInit(FMLPostInitializationEvent event){

    }

    @Override
    public void spawnMagicParticle(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, int color, float scale, int maxAge, float gravity, boolean collision){
        ParticleMagic particle = new ParticleMagic(world, posX, posY, posZ, motionX, motionY, motionZ, color, scale, maxAge, gravity, collision);
        ParticleHandler.spawnParticle(particle, posX, posY, posZ, 32);
    }
}
