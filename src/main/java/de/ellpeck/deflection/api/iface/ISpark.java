package de.ellpeck.deflection.api.iface;

public interface ISpark{

    double getX();

    double getY();

    double getZ();

    double getMotionX();

    double getMotionY();

    double getMotionZ();

    void setPosition(double x, double y, double z);

    void setMotion(double x, double y, double z);
}
