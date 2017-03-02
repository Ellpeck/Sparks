package de.ellpeck.sparks.mod.event;

import de.ellpeck.sparks.api.cap.IPotentialHandler;
import de.ellpeck.sparks.api.cap.SparksCapabilities;
import de.ellpeck.sparks.mod.item.ItemMultitool;
import de.ellpeck.sparks.mod.item.ItemMultitool.ToolAttachment;
import de.ellpeck.sparks.mod.particle.ParticleHandler;
import de.ellpeck.sparks.mod.particle.ParticleMagic;
import de.ellpeck.sparks.mod.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
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
        Minecraft mc = Minecraft.getMinecraft();

        if(!mc.isGamePaused()){
            ParticleHandler.updateParticles();
        }

        if(mc.world == null){
            ParticleHandler.clearParticles();
        }
    }

    @SubscribeEvent
    public void onGameOverlay(RenderGameOverlayEvent.Post event){
        if(event.getType() == ElementType.ALL){
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.currentScreen == null){
                ScaledResolution res = event.getResolution();
                EntityPlayer player = mc.player;
                RayTraceResult posHit = mc.objectMouseOver;
                FontRenderer font = mc.fontRendererObj;
                ItemStack stack = player.getHeldItemMainhand();

                if(stack != null && stack.getItem() instanceof ItemMultitool){
                    int yStart = (res.getScaledHeight()/3)*2;

                    if(ItemMultitool.hasAttachment(stack, ToolAttachment.GAUGE) && posHit != null){
                        BlockPos pos = posHit.getBlockPos();
                        if(pos != null){
                            TileEntity tile = mc.world.getTileEntity(pos);

                            if(tile != null && tile.hasCapability(SparksCapabilities.capabilityPotential, null)){
                                IPotentialHandler cap = tile.getCapability(SparksCapabilities.capabilityPotential, null);
                                if(cap != null){
                                    int amount = cap.getPotential();
                                    int maxAmount = cap.getMaxPotential();

                                    String s = "Stored: "+amount+"/"+maxAmount;
                                    int x = res.getScaledWidth()/2-font.getStringWidth(s)/2;
                                    font.drawStringWithShadow(s, x, yStart, 0xFFFFFF);
                                }
                            }
                        }
                    }

                    BlockPos pos = ItemMultitool.getStoredPos(stack);
                    if(pos != null){
                        String s = TextFormatting.GOLD+"Connecting to: "+pos.getX()+", "+pos.getY()+", "+pos.getZ();
                        int x = res.getScaledWidth()/2-font.getStringWidth(s)/2;
                        font.drawStringWithShadow(s, x, yStart+15, 0xFFFFFF);
                    }
                }
            }
        }
    }
}