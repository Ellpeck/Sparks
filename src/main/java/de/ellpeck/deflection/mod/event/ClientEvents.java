package de.ellpeck.deflection.mod.event;

import de.ellpeck.deflection.mod.particle.ParticleHandler;
import de.ellpeck.deflection.mod.particle.ParticleMagic;
import de.ellpeck.deflection.mod.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientEvents{

    @SubscribeEvent
    public void onDebugRender(RenderGameOverlayEvent.Text event){
        if(Minecraft.getMinecraft().gameSettings.showDebugInfo){
            List<String> left = event.getLeft();
            String prefix = TextFormatting.GREEN+"["+ModUtil.NAME+"]"+TextFormatting.RESET+" ";

            left.add("");
            left.add(prefix+"PartScrn: "+ParticleHandler.getParticleAmount());
        }
    }

    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event){
        ParticleHandler.renderParticles(event.getPartialTicks());
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent event){
        TextureMap map = event.getMap();
        map.registerSprite(ParticleMagic.RES_LOC);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event){
        if(!Minecraft.getMinecraft().isGamePaused()){
            ParticleHandler.updateParticles();
        }
    }
}