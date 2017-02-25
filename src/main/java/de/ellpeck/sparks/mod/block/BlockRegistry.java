package de.ellpeck.sparks.mod.block;

import net.minecraft.block.Block;

public final class BlockRegistry{

    public static Block blockSparkInitiator;
    public static Block blockMirror;
    public static Block blockSplitter;
    public static Block blockPickerUpper;

    public static void preInit(){
        blockSparkInitiator = new BlockSparkInitiator();
        blockMirror = new BlockMirror();
        blockSplitter = new BlockSplitter();
        blockPickerUpper = new BlockBurningCreator();
    }

}
