package de.ellpeck.deflection.mod.block;

import net.minecraft.block.Block;

public final class BlockRegistry{

    public static Block blockSparkInitiator;
    public static Block blockMirror;

    public static void preInit(){
        blockSparkInitiator = new BlockSparkInitiator();
        blockMirror = new BlockMirror();
    }

}
