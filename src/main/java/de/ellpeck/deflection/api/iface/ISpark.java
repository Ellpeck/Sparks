package de.ellpeck.deflection.api.iface;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface ISpark{

    double getX();

    double getY();

    double getZ();

    double getMotion();

    EnumFacing getFacing();

    void setPos(double x, double y, double z);

    void setMotion(double motion);

    void setFacing(EnumFacing facing);

    void setColor(int color);

    int getColor();

    BlockPos getLastInteractor();

    void setLastInteractor(BlockPos pos);

    boolean split(ISparkInteractor splitter, BlockPos pos, IBlockState state, EnumFacing firstDir, EnumFacing secondDir);
}
