package de.ellpeck.sparks.mod.item;

import net.minecraft.item.Item;

public final class ItemRegistry{

    public static Item itemMultitool;

    public static void preInit(){
        itemMultitool = new ItemMultitool();
    }

}
