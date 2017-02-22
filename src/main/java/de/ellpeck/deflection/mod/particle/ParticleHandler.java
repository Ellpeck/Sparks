package de.ellpeck.deflection.mod.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ParticleHandler{

    public static void spawnParticle(Particle particle, double x, double y, double z, int range){
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.player.getDistanceSq(x, y, z) <= range*range){
            mc.effectRenderer.addEffect(particle);
        }
    }
}