package de.ellpeck.deflection.mod.event;

import de.ellpeck.deflection.mod.particle.ParticleMagic;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEvents{

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent event){
        TextureMap map = event.getMap();
        map.registerSprite(ParticleMagic.RES_LOC);
    }

}
