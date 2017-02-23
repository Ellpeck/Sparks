package de.ellpeck.deflection.mod.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class PacketHandler{

    private static final int PACKET_RANGE = 64;

    public static void dispatchVanilla(TileEntity tile){
        World world = tile.getWorld();
        if(!world.isRemote){
            SPacketUpdateTileEntity packet = tile.getUpdatePacket();
            if(packet != null){
                BlockPos tilePos = tile.getPos().add(0.5, 0.5, 0.5);

                for(EntityPlayer player : world.playerEntities){
                    if(player instanceof EntityPlayerMP){
                        if(tilePos.distanceSq(player.posX, player.posY, player.posZ) <= PACKET_RANGE*PACKET_RANGE){
                            ((EntityPlayerMP)player).connection.sendPacket(packet);
                        }
                    }
                }
            }
        }
    }
}
