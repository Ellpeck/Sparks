package de.ellpeck.sparks.mod.util;

import de.ellpeck.sparks.mod.block.BlockRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTab extends CreativeTabs{

    public static final CreativeTab INSTANCE = new CreativeTab();

    public CreativeTab(){
        super(ModUtil.MOD_ID);
    }

    @Override
    public Item getTabIconItem(){
        return Item.getItemFromBlock(BlockRegistry.blockMirror);
    }
}
