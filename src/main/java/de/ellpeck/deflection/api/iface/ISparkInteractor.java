package de.ellpeck.deflection.api.iface;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISparkInteractor{

    boolean interact(World world, BlockPos pos, IBlockState state, ISpark spark);

}
