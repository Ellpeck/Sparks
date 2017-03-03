package de.ellpeck.sparks.api.iface;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISparkInteractor{

    EnumActionResult interactWithSpark(World world, BlockPos pos, IBlockState state, ISpark spark);

}
