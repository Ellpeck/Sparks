package de.ellpeck.sparks.mod.item;

import net.minecraft.item.Item;

public final class ItemRegistry{

    public static Item itemStaff;

    public static void preInit(){
        itemStaff = new ItemStaff();
    }

}
