package de.ellpeck.deflection.mod.entity.render;

import de.ellpeck.deflection.mod.entity.spark.EntityPickupSpark;
import de.ellpeck.deflection.mod.util.ClientUtil;
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
public class RenderPickupSpark extends Render<EntityPickupSpark>{

    public static final IRenderFactory FACTORY = new IRenderFactory<EntityPickupSpark>(){
        @Override
        public Render<? super EntityPickupSpark> createRenderFor(RenderManager manager){
            return new RenderPickupSpark(manager);
        }
    };

    protected RenderPickupSpark(RenderManager manager){
        super(manager);
    }

    @Override
    public void doRender(EntityPickupSpark entity, double x, double y, double z, float entityYaw, float partialTicks){
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        ItemStack stack = entity.getCarryingStack();
        if(stack != null){
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y+0.3F, (float)z);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);

            double boop = Minecraft.getSystemTime()/1000D;
            GlStateManager.translate(0D, Math.sin(boop%(2*Math.PI))*0.25, 0D);
            GlStateManager.rotate((float)(((boop*40D)%360)), 0, 1, 0);

            ClientUtil.renderItemInWorld(stack);

            GlStateManager.popMatrix();
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPickupSpark entity){
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
