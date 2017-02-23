package de.ellpeck.deflection.mod.packet;

import de.ellpeck.deflection.mod.Deflection;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class PacketParticleExplosion implements IMessage{

    private double x;
    private double y;
    private double z;
    private int color;
    private int amount;
    private double area;
    private float scale;
    private boolean collision;

    public PacketParticleExplosion(){

    }

    public PacketParticleExplosion(double x, double y, double z, int color, int amount, double area, float scale, boolean collision){
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.amount = amount;
        this.area = area;
        this.scale = scale;
        this.collision = collision;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.color = buf.readInt();
        this.amount = buf.readInt();
        this.area = buf.readDouble();
        this.scale = buf.readFloat();
        this.collision = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf){
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeInt(this.color);
        buf.writeInt(this.amount);
        buf.writeDouble(this.area);
        buf.writeFloat(this.scale);
        buf.writeBoolean(this.collision);
    }

    public static class Handler implements IMessageHandler<PacketParticleExplosion, IMessage>{

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(final PacketParticleExplosion message, MessageContext ctx){
            Deflection.proxy.scheduleTask(new Runnable(){
                @Override
                public void run(){
                    Minecraft mc = Minecraft.getMinecraft();
                    Random rand = mc.world.rand;

                    for(int i = 0; i < message.amount; i++){
                        double x = message.x+rand.nextGaussian()*message.area;
                        double y = message.y+rand.nextGaussian()*message.area;
                        double z = message.z+rand.nextGaussian()*message.area;

                        double motionX = rand.nextGaussian()*message.area;
                        double motionY = rand.nextGaussian()*message.area;
                        double motionZ = rand.nextGaussian()*message.area;

                        Deflection.proxy.spawnMagicParticle(mc.world, x, y, z, motionX, motionY, motionZ, message.color, message.scale, 50, 0F, message.collision);
                    }
                }
            });
            return null;
        }
    }
}
