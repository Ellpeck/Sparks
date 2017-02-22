package de.ellpeck.deflection.mod.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTab extends CreativeTabs{

    public static CreativeTab INSTANCE = new CreativeTab();

    public CreativeTab(){
        super(ModUtil.MOD_ID);
    }

    @Override
    public ItemStack getTabIconItem(){
        return ItemStack.EMPTY;
    }
}
