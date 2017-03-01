package de.ellpeck.sparks.mod.entity.render;

import de.ellpeck.sparks.mod.entity.spark.base.EntityTravellingSparkBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSparkBase extends Render<EntityTravellingSparkBase>{

    public static final IRenderFactory FACTORY = new IRenderFactory<EntityTravellingSparkBase>(){
        @Override
        public Render<? super EntityTravellingSparkBase> createRenderFor(RenderManager manager){
            return new RenderSparkBase(manager);
        }
    };

    protected RenderSparkBase(RenderManager manager){
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTravellingSparkBase entity){
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
