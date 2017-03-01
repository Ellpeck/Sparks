package de.ellpeck.sparks.mod.block;

import net.minecraft.block.Block;

public final class BlockRegistry{

    public static Block blockMirror;
    public static Block blockSplitter;
    public static Block blockPickerUpper;
    public static Block blockPotentialSparkInitiator;

    public static void preInit(){
        blockMirror = new BlockMirror();
        blockSplitter = new BlockSplitter();
        blockPickerUpper = new BlockBurningCreator();
        blockPotentialSparkInitiator = new BlockPotentialSparkInitiator();
    }

}
