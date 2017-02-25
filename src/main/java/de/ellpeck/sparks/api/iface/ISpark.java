package de.ellpeck.sparks.api.iface;

import net.minecraft.util.math.BlockPos;

public interface ISpark{

    double getX();

    double getY();

    double getZ();

    void setPos(double x, double y, double z);

    void setColor(int color);

    int getColor();

    BlockPos getLastInteractor();

    void setLastInteractor(BlockPos pos);

    void setKilled();
}
