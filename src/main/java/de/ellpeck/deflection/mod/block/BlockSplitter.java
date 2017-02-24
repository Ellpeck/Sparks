package de.ellpeck.deflection.mod.block;

import de.ellpeck.deflection.api.iface.ISpark;
import de.ellpeck.deflection.api.iface.ISparkInteractor;
import de.ellpeck.deflection.mod.packet.PacketHandler;
import de.ellpeck.deflection.mod.packet.PacketParticleExplosion;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSplitter extends BlockBase implements ISparkInteractor{

    private static final float F = 1F/16F;
    private static final AxisAlignedBB AABB = new AxisAlignedBB(3*F, 0, 3*F, 1-3*F, 1-F, 1-3*F);

    public BlockSplitter(){
        super(Material.ROCK, "splitter");
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return AABB;
    }

    @Override
    public boolean interact(World world, BlockPos pos, IBlockState state, ISpark spark){
        EnumFacing facing = spark.getFacing();

        double x = spark.getX();
        double y = spark.getY();
        double z = spark.getZ();

        if(facing.getAxis().isHorizontal()){
            if(!pos.equals(spark.getLastInteractor())){
                if(x >= pos.getX()+0.45 && x <= pos.getX()+0.55 && y >= pos.getY()+0.45 && y <= pos.getY()+0.55 && z >= pos.getZ()+0.45 && z <= pos.getZ()+0.55){
                    if(spark.split(this, pos, state, facing.rotateY(), facing.rotateYCCW())){
                        PacketParticleExplosion packet = new PacketParticleExplosion(x, y, z, spark.getColor(), 15, 0.02, 2.5F, false);
                        PacketHandler.sendToAllAround(world, pos, packet);

                        return true;
                    }
                    else{
                        return false;
                    }
                }
            }
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean isFullCube(IBlockState state){
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer(){
        return BlockRenderLayer.TRANSLUCENT;
    }
}
