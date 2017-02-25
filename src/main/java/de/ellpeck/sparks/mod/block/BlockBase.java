package de.ellpeck.sparks.mod.block;

import de.ellpeck.sparks.mod.reg.IModItem;
import de.ellpeck.sparks.mod.reg.IModelProvider;
import de.ellpeck.sparks.mod.reg.ModRegistry;
import de.ellpeck.sparks.mod.util.ModUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Collections;
import java.util.Map;

public class BlockBase extends Block implements IModItem, IModelProvider{

    private final String baseName;

    public BlockBase(Material material, String baseName){
        super(material);
        this.baseName = baseName;

        ModRegistry.addItemOrBlock(this);
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

    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event){

    }

    @Override
    public Map<ItemStack, ModelVariant> getModelLocations(){
        return Collections.singletonMap(new ItemStack(this), new ModelVariant(new ResourceLocation(ModUtil.MOD_ID, this.getBaseName()), "inventory"));
    }
}