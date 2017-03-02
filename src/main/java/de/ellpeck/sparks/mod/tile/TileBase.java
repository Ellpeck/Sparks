package de.ellpeck.sparks.mod.tile;

import de.ellpeck.sparks.api.cap.IPotentialHandler;
import de.ellpeck.sparks.api.cap.SparksCapabilities;
import de.ellpeck.sparks.mod.packet.PacketHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TileBase extends TileEntity{

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound){
        this.writeSyncedNBT(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound){
        this.readSyncedNBT(compound);
        super.readFromNBT(compound);
    }

    public void writeSyncedNBT(NBTTagCompound compound){

    }

    public void readSyncedNBT(NBTTagCompound compound){

    }

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket(){
        NBTTagCompound compound = new NBTTagCompound();
        this.writeSyncedNBT(compound);
        return new SPacketUpdateTileEntity(this.pos, 0, compound);
    }

    @Override
    public final NBTTagCompound getUpdateTag(){
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet){
        super.onDataPacket(net, packet);
        this.readSyncedNBT(packet.getNbtCompound());
    }

    public void sendToClient(){
        PacketHandler.dispatchVanilla(this);
    }

    protected IPotentialHandler getPotentialHandler(){
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        if(capability == SparksCapabilities.capabilityPotential){
            return this.getPotentialHandler() != null;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing){
        if(capability == SparksCapabilities.capabilityPotential){
            IPotentialHandler handler = this.getPotentialHandler();
            if(handler != null){
                return (T)handler;
            }
        }
        return super.getCapability(capability, facing);
    }
}