package de.ellpeck.deflection.mod.packet;

import de.ellpeck.deflection.mod.util.ModUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class PacketHandler{

    private static final int PACKET_RANGE = 64;

    private static SimpleNetworkWrapper wrapper;

    public static void preInit(){
        wrapper = new SimpleNetworkWrapper(ModUtil.MOD_ID);

        wrapper.registerMessage(PacketParticleExplosion.Handler.class, PacketParticleExplosion.class, 0, Side.CLIENT);
    }

    public static void sendToAllAround(World world, BlockPos pos, IMessage packet){
        TargetPoint point = new TargetPoint(world.provider.getDimension(), pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, PACKET_RANGE);
        wrapper.sendToAllAround(packet, point);
    }

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
