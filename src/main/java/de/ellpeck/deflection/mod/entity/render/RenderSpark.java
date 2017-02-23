package de.ellpeck.deflection.mod.entity.render;

import de.ellpeck.deflection.mod.entity.spark.EntitySpark;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSpark extends Render<EntitySpark>{

    public static final IRenderFactory FACTORY = new IRenderFactory<EntitySpark>(){
        @Override
        public Render<? super EntitySpark> createRenderFor(RenderManager manager){
            return new RenderSpark(manager);
        }
    };

    protected RenderSpark(RenderManager manager){
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySpark entity){
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
