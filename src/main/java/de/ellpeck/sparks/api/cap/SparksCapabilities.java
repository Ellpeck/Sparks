package de.ellpeck.sparks.api.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;

public final class SparksCapabilities{

    @CapabilityInject(IPotentialHandler.class)
    public static Capability<IPotentialHandler> capabilityPotential;

    public static class CapabilityPotential implements IStorage<IPotentialHandler>{

        @Override
        public NBTBase writeNBT(Capability<IPotentialHandler> capability, IPotentialHandler instance, EnumFacing side){
            return null;
        }

        @Override
        public void readNBT(Capability<IPotentialHandler> capability, IPotentialHandler instance, EnumFacing side, NBTBase nbt){

        }
    }
}
