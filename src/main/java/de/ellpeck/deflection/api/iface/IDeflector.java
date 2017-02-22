package de.ellpeck.deflection.api.iface;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDeflector{

    void deflect(World world, BlockPos pos, IBlockState state, ISpark spark);

}
