package de.ellpeck.deflection.mod.block;

import de.ellpeck.deflection.mod.reg.IModItem;
import de.ellpeck.deflection.mod.reg.IModelProvider;
import de.ellpeck.deflection.mod.reg.ModRegistry;
import de.ellpeck.deflection.mod.util.ModUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Collections;
import java.util.Map;

public class BlockContainerBase extends BlockContainer implements IModItem, IModelProvider{

    private final String baseName;

    private final Class<? extends TileEntity> tileClass;
    private final String tileRegName;

    public BlockContainerBase(Material material, String baseName, Class<? extends TileEntity> tileClass, String tileReg){
        super(material);

        this.baseName = baseName;
        this.tileClass = tileClass;
        this.tileRegName = tileReg;

        ModRegistry.addItemOrBlock(this);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta){
        try{
            return this.tileClass.newInstance();
        }
        catch(Exception e){
            return null;
        }
    }

    @Override
    public String getBaseName(){
        return this.baseName;
    }

    @Override
    public boolean shouldAddCreative(){
        return true;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event){
    }

    @Override
    public void onInit(FMLInitializationEvent event){
        GameRegistry.registerTileEntity(this.tileClass, ModUtil.MOD_ID+":"+this.tileRegName);
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event){

    }

    @Override
    public Map<ItemStack, ModelVariant> getModelLocations(){
        return Collections.singletonMap(new ItemStack(this), new ModelVariant(new ResourceLocation(ModUtil.MOD_ID, this.getBaseName()), "inventory"));
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state){
        return EnumBlockRenderType.MODEL;
    }
}