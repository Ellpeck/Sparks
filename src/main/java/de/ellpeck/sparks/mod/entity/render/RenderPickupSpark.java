package de.ellpeck.sparks.mod.entity.render;

import de.ellpeck.sparks.mod.entity.spark.pickup.EntityItemPickupSpark;
import de.ellpeck.sparks.mod.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPickupSpark extends Render<EntityItemPickupSpark>{

    public static final IRenderFactory FACTORY = new IRenderFactory<EntityItemPickupSpark>(){
        @Override
        public Render<? super EntityItemPickupSpark> createRenderFor(RenderManager manager){
            return new RenderPickupSpark(manager);
        }
    };

    protected RenderPickupSpark(RenderManager manager){
        super(manager);
    }

    @Override
    public void doRender(EntityItemPickupSpark entity, double x, double y, double z, float entityYaw, float partialTicks){
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        ItemStack stack = entity.getCarryingStack();
        if(stack != null){
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y+0.2F, (float)z);
            GlStateManager.scale(0.3F, 0.3F, 0.3F);

            double boop = Minecraft.getSystemTime()/1000D;
            GlStateManager.translate(0D, Math.sin(boop%(2*Math.PI))*0.25, 0D);
            GlStateManager.rotate((float)(((boop*40D)%360)), 0, 1, 0);

            ClientUtil.renderItemInWorld(stack);

            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityItemPickupSpark entity){
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
