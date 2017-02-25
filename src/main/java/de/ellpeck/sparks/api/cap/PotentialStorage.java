package de.ellpeck.sparks.api.cap;

import net.minecraft.nbt.NBTTagCompound;

public class PotentialStorage implements IPotentialHandler{

    private final int maxAmount;
    private final int maxReceive;
    private final int maxExtract;

    private int amount;

    public PotentialStorage(int maxAmount, int maxReceive, int maxExtract){
        this.maxAmount = maxAmount;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public int getPotential(){
        return this.amount;
    }

    @Override
    public int getMaxPotential(){
        return this.maxAmount;
    }

    @Override
    public int receivePotential(int amount, boolean simulate){
        return this.receiveInternal(Math.min(this.maxReceive, amount), simulate);
    }

    @Override
    public int extractPotential(int amount, boolean simulate){
        return this.extractInternal(Math.min(this.maxExtract, amount), simulate);
    }

    public boolean isNearlyFull(int play){
        int space = this.maxAmount-this.amount;
        return space <= play;
    }

    public int receiveInternal(int amount, boolean simulate){
        int toReceive = Math.min(this.maxAmount-this.amount, amount);
        if(toReceive > 0){
            if(!simulate){
                this.amount += toReceive;
            }
        }
        return toReceive;
    }

    public int extractInternal(int amount, boolean simulate){
        int toExtract = Math.min(this.amount, amount);
        if(toExtract > 0){
            if(!simulate){
                this.amount -= toExtract;
            }
        }
        return toExtract;
    }

    public void writeToNBT(NBTTagCompound compound){
        compound.setInteger("Potential", this.amount);
    }

    public void readFromNBT(NBTTagCompound compound){
        this.amount = compound.getInteger("Potential");
    }
}
