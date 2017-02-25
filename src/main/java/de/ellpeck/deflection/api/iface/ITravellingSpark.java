package de.ellpeck.deflection.api.iface;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface ITravellingSpark extends ISpark{

    double getMotion();

    EnumFacing getFacing();

    void setMotion(double motion);

    void setFacing(EnumFacing facing);

    boolean split(ISparkInteractor splitter, BlockPos pos, IBlockState state, EnumFacing firstDir, EnumFacing secondDir);
}
