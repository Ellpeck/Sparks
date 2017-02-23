package de.ellpeck.deflection.api.iface;

import net.minecraft.util.EnumFacing;

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
}
