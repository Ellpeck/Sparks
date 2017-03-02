package de.ellpeck.sparks.api.cap;

public interface IPotentialHandler{

    int getPotential();

    int getMaxPotential();

    int receivePotential(int amount, boolean simulate);

    int extractPotential(int amount, boolean simulate);

    int getMaxReceive();

    int getMaxExtract();
}
