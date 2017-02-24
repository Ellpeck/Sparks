package de.ellpeck.deflection.mod.block;

import de.ellpeck.deflection.api.iface.ISpark;
import de.ellpeck.deflection.api.iface.ISparkInteractor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSplitter extends BlockBase implements ISparkInteractor{

    public BlockSplitter(){
        super(Material.ROCK, "splitter");
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
                    return spark.split(this, pos, state, facing.rotateY(), facing.rotateYCCW());
                }
            }
            return true;
        }
        else{
            return false;
        }
    }
}
